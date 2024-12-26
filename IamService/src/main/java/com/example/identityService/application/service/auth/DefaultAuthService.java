package com.example.identityService.application.service.auth;

import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.response.ClientTokenResponse;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.service.FileService;
import com.devdeli.common.service.RedisService;
import com.example.identityService.application.DTO.EmailEnum;
import com.example.identityService.application.DTO.EnumRole;
import com.example.identityService.application.DTO.request.ChangePasswordRequest;
import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.EmailRequest;
import com.example.identityService.application.DTO.request.LoginRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.UpdateProfileRequest;
import com.example.identityService.application.DTO.response.LoginResponse;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.util.TimeConverter;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.infrastructure.persistence.entity.LogEntity;
import com.example.identityService.application.DTO.Token;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.infrastructure.persistence.mapper.AccountMapper;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
import com.example.identityService.infrastructure.persistence.repository.LoggerRepository;
import com.example.identityService.application.service.AccountRoleService;
import com.example.identityService.application.service.EmailService;
import com.example.identityService.application.service.TokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DefaultAuthService extends AbstractAuthService {

    @Value(value = "${app.baseUrl}")
    private String APP_BASEURL;
    @Value(value = "${app.storage.internal-url}")
    private String STORAGE_BASEURL;

    @Value(value = "${security.authentication.max-forgot-password-attempt}")
    private Integer MAX_FORGOT_PASSWORD_ATTEMPT;
    @Value(value = "${security.authentication.delay-forgot-password}")
    private String DELAY_FORGOT_PASSWORD;

    @Value(value = "${security.authentication.jwt.access-token-life-time}")
    private String ACCESS_TOKEN_LIFE_TIME;
    @Value(value = "${security.authentication.jwt.refresh-token-life-time}")
    private String REFRESH_TOKEN_LIFE_TIME;
    @Value(value = "${security.authentication.jwt.email-token-life-time}")
    private String EMAIL_TOKEN_LIFE_TIME;

    private final AccountRepository accountRepository;
    private final TokenService tokenService;

    private final EmailService emailService;
    private final LoggerRepository loggerRepository;
    private final FileService fileService;
    private final RedisService redisService;

    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    private final AccountRoleService accountRoleService;

    // -----------------------------Login logout start-------------------------------
    @Override
    public LoginResponse performLogin(LoginRequest request){
       return loginProcess(request.getEmail(), request.getIp());
    }

    @Override
    public ClientTokenResponse performGetClientToken(ClientTokenRequest request) {
        return new ClientTokenResponse(tokenService.generateTempEmailToken(request.getClientId(),""));
    }

    @Override
    public LoginResponse performLoginWithGoogle(String email, String password, String ip){
        return loginProcess(email, ip);
    }

    @Override
    public boolean logout(String accessToken, String refreshToken) throws ParseException {
        boolean isDisabledAccessToken = tokenService.deActiveToken(new Token(accessToken,
                TimeConverter.convertToMilliseconds(ACCESS_TOKEN_LIFE_TIME)));
        boolean isDisabledRefreshToken = tokenService.deActiveToken(new Token(refreshToken,
                TimeConverter.convertToMilliseconds(REFRESH_TOKEN_LIFE_TIME)));
        return  isDisabledAccessToken && isDisabledRefreshToken;
    }

    public LoginResponse loginProcess(String email, String ip){
        AccountEntity foundAccount = accountRepository.findByEmail(email)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        String accessToken = tokenService.accessTokenFactory(foundAccount);
        String refreshToken = tokenService.generateRefreshToken(email, ip);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // -----------------------------Login logout end-------------------------------

    // -----------------------------Registration flow start-------------------------------

    @Override
    public boolean performRegister(RegisterRequest request) {
        accountRepository
                .findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new AppExceptions(ErrorCode.USER_EXISTED);
                });
        AccountEntity newAccount = accountMapper.toAccount(request);
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        newAccount.setEnable(true);

        createAppUserAndAssignRole(newAccount, request.getIp());

        sendVerifyEmail(newAccount.getEmail(), request.getIp());
        return true;
    }

    @Override
    public boolean performCreateUser(CreateAccountRequest request) {
        accountRepository
                .findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new AppExceptions(ErrorCode.USER_EXISTED);
                });
        AccountEntity newAccount = accountMapper.toAccount(request);
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));

        AccountEntity savedAccount = accountRepository.save(newAccount);
        return accountRoleService
                .assignRolesForUser(savedAccount.getId(), request.getRoles());
    }

    @Override
    public boolean performRegisterUserFromGoogle(AccountEntity request, String ip) {
        createAppUserAndAssignRole(request, ip);
        return true;
    }

    public Object verifyEmailAndIP(String token, String ip){
        Claims claims = tokenService.extractClaims(token);
        if(claims == null) throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        String email = claims.getSubject();
        String ipFromToken = claims.get("IP").toString();
        if(!tokenService.verifyToken(token) || !ip.equals(ipFromToken))
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        boolean foundLog = loggerRepository
                .existsByEmailAndIp(email, ipFromToken);

        AccountEntity account = getAccountByEmail(email);
        if(foundLog){
            account.setVerified(true);
            accountRepository.save(account);
            return true;
        }

        loggerRepository.save(LogEntity.builder()
                .actionName("CONFIRM_IP")
                .email(email)
                .ip(ip)
                .build());

        return loginProcess(account.getEmail(), ip);
    }
    // -----------------------------Registration flow end-------------------------------

    // -----------------------------User information start-------------------------------
    // profile
    public UserResponse getProfile() {
        AccountEntity foundUser = getCurrentUser();
        return accountMapper.toUserResponse(foundUser);
    }

    public boolean updateProfile(UpdateProfileRequest request, MultipartFile image) throws IOException {
        AccountEntity foundUser = getCurrentUser();
        if (request != null) {
            accountMapper.updateAccount(foundUser, request);
            if (request.getCloudImageUrl() != null) {
                String oldCloudId = foundUser.getCloudImageId();
                if (oldCloudId != null) {
                    fileService.deletePublicFile(oldCloudId);
                }
                foundUser.setCloudImageUrl(request.getCloudImageUrl());
                foundUser.setCloudImageId(null);
                accountRepository.save(foundUser);
                return true;
            }
        }
        if (image != null) {
            String oldCloudId = foundUser.getCloudImageId();
            if (oldCloudId != null) {
                fileService.deletePublicFile(oldCloudId);
            }
            FileResponse fileResponse = fileService.uploadPublicFiles(List.of(image), foundUser.getEmail()).getFirst();
            foundUser.setCloudImageId(fileResponse.getPath());
            foundUser.setCloudImageUrl(STORAGE_BASEURL + "/api/v1.0.0/public/files/" + fileResponse.getPath());
        }
        accountRepository.save(foundUser);
        return true;
    }

    // password
    @Override
    public boolean performChangePassword(ChangePasswordRequest request, String ip) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        if(currentPassword.equals(newPassword)) throw new AppExceptions(ErrorCode.PASSWORD_MUST_DIFFERENCE);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        AccountEntity foundUser = accountRepository.findByEmail(email)
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        boolean isCorrectPassword = passwordEncoder.matches(request.getCurrentPassword(), foundUser.getPassword());
        if(!isCorrectPassword) throw new AppExceptions(ErrorCode.WRONG_PASSWORD);

        foundUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(foundUser);

        loggerRepository.save(LogEntity.builder()
                .actionName("CHANGE_PASSWORD")
                .email(foundUser.getEmail())
                .ip(ip)
                .build());
        return true;
    }

    public boolean forgotPassword(String email, String ip) {
        getAccountByEmail(email);
        sendForgotPasswordEmail(email, ip);
        return true;
    }

    @Override
    public boolean performResetPassword(String token, String newPassword, String ip) throws ParseException {
        String email = tokenService.getTokenDecoded(token).getSubject();

        String key = String.join("","forgot-password-attempt:", email);
        String attempValueString = redisService.getValue(key);

        String activeToken = attempValueString != null ? attempValueString.split("@")[1] : null;
        if(!tokenService.verifyToken(token) || email == null || !Objects.equals(activeToken, token))
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        tokenService.deActiveToken(new Token(token, TimeConverter.convertToMilliseconds(EMAIL_TOKEN_LIFE_TIME)));

        AccountEntity foundAccount = accountRepository.findByEmail(email)
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        foundAccount.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(foundAccount);

        loggerRepository.save(LogEntity.builder()
                .actionName("RESET_PASSWORD")
                .email(email)
                .ip(ip)
                .build());

        sendResetPasswordSuccess(email);
        return true;
    }

    public void sendForgotPasswordEmail(String email, String ip){
        String forgotPasswordToken = tokenService.generateTempEmailToken(email, ip);

        String key = String.join("","forgot-password-attempt:", email);
        String attempValueString = redisService.getValue(key);

        Integer attemptTime = attempValueString != null ? Integer.parseInt(attempValueString.split("@")[0])+1 : 1;

        if(attemptTime.equals(MAX_FORGOT_PASSWORD_ATTEMPT+1))
            throw new AppExceptions(ErrorCode.TOO_MUCH_FORGOT_PASSWORD_ATTEMPT);

        String value = String.join("@", attemptTime.toString(), forgotPasswordToken);

        redisService.putValue(key, value,
                Duration.ofMillis(TimeConverter.convertToMilliseconds(DELAY_FORGOT_PASSWORD)));

        String verifyUrl = String.join("",APP_BASEURL,"auth/resetPassword?token=",forgotPasswordToken);
        emailService
                .sendEmail(new EmailRequest(EmailEnum.FORGOT_PASSWORD.getSubject(),
                        String.join(" ",EmailEnum.FORGOT_PASSWORD.getContent(), verifyUrl)
                        ,List.of(email)));
    }

    // -----------------------------User information end-------------------------------

    // -----------------------------Utilities start-------------------------------
    public AccountEntity getAccountByEmail(String email){
        return accountRepository.findByEmail(email)
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
    }

    public AccountEntity getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email.equals("anonymousUser")) throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        return getAccountByEmail(email);
    }

    @Override
    public String getNewToken(String refreshToken){
        if(!tokenService.verifyToken(refreshToken) ||
                Objects.isNull(tokenService.getTokenDecoded(refreshToken).getSubject())){
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        String email = tokenService.getTokenDecoded(refreshToken).getSubject();
        AccountEntity foundAccount = getAccountByEmail(email);
        return tokenService.accessTokenFactory(foundAccount);
    }


    public void createAppUserAndAssignRole(AccountEntity account, String ip){
        AccountEntity savedAccount = accountRepository.save(account);
        loggerRepository.save(LogEntity.builder()
                .actionName("REGISTRATION")
                .email(savedAccount.getEmail())
                .ip(ip)
                .build());
        accountRoleService.assignRolesForUser(savedAccount.getId(), List.of(EnumRole.USER.getName()));
    }

    public boolean introspect(String token) {
        return tokenService.verifyToken(token);
    }
    // -----------------------------Utilities end-------------------------------
}
