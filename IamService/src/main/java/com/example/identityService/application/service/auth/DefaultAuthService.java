package com.example.identityService.application.service.auth;

import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.response.ClientTokenResponse;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.service.FileService;
import com.devdeli.common.service.RedisService;
import com.example.identityService.application.DTO.EmailEnum;
import com.example.identityService.application.DTO.Token;
import com.example.identityService.application.DTO.request.*;
import com.example.identityService.application.DTO.response.LoginResponse;
import com.example.identityService.application.DTO.response.UserResponse;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.application.service.AccountRoleService;
import com.example.identityService.application.service.EmailService;
import com.example.identityService.application.service.TokenService;
import com.example.identityService.application.util.TimeConverter;
import com.example.identityService.domain.User;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.infrastructure.persistence.mapper.AccountMapper;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
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

    private final AccountRepository accountRepository;
    private final TokenService tokenService;

    private final EmailService emailService;
    private final FileService fileService;
    private final RedisService redisService;

    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    private final AccountRoleService accountRoleService;

    // -----------------------------Login logout start-------------------------------
    @Override
    public LoginResponse performLogin(LoginRequest request){
       return loginProcess(request.getEmail());
    }

    @Override
    public ClientTokenResponse performGetClientToken(ClientTokenRequest request) {
        return new ClientTokenResponse(tokenService.generateTempEmailToken(request.getClientId()));
    }

    @Override
    public LoginResponse performLoginWithGoogle(String email, String password){
        return loginProcess(email);
    }

    @Override
    public boolean logout(String accessToken, String refreshToken) throws ParseException {
        boolean isDisabledAccessToken = tokenService.deActiveToken(new Token(accessToken,
                TimeConverter.convertToMilliseconds(ACCESS_TOKEN_LIFE_TIME)));
        boolean isDisabledRefreshToken = tokenService.deActiveToken(new Token(refreshToken,
                TimeConverter.convertToMilliseconds(REFRESH_TOKEN_LIFE_TIME)));
        return  isDisabledAccessToken && isDisabledRefreshToken;
    }

    public LoginResponse loginProcess(String email){
        AccountEntity foundAccount = accountRepository.findByEmail(email)
                .orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        String accessToken = tokenService.accessTokenFactory(foundAccount);
        String refreshToken = tokenService.generateRefreshToken(email);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // -----------------------------Login logout end-------------------------------

    // -----------------------------Registration flow start-------------------------------
    @Override
    public boolean performCreateOrUpdateUser(User request) {
        AccountEntity user = accountMapper.toAccount(request);
        AccountEntity existedUser = accountRepository.findById(request.getId())
                .orElseGet(AccountEntity::new);
        if (!passwordEncoder.matches(request.getPassword(), existedUser.getPassword()))
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(user);
        return true;
    }

    @Override
    public boolean performCreateOrUpdateUsers(List<User> request) {
        List<AccountEntity> users = accountMapper.toAccount(request);
        List<AccountEntity> existedUsers = accountRepository.getByIdIn(users.stream().map(AccountEntity::getId).toList());
        for(int i = 0; i < existedUsers.size(); i++) {
            if (!passwordEncoder.matches(request.get(i).getPassword(), existedUsers.get(i).getPassword()))
                existedUsers.get(i).setPassword(passwordEncoder.encode(request.get(i).getPassword()));
        }
        accountRepository.saveAll(users);
        return true;
    }

    @Override
    public boolean performRegisterUserFromGoogle(AccountEntity request) {
        createAppUserAndAssignRole(request);
        return true;
    }

    public Object verifyEmailAndIP(String token){
        Claims claims = tokenService.extractClaims(token)
                .orElseThrow(()-> new AppExceptions(ErrorCode.UNAUTHENTICATED));
        String email = claims.getSubject();
        if(!tokenService.verifyToken(token))
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        AccountEntity account = getAccountByEmail(email);
        if(true){
            account.setVerified(true);
            accountRepository.save(account);
            return true;
        }
        return loginProcess(account.getEmail());
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
    public boolean forgotPassword(String email) {
        getAccountByEmail(email);
        sendForgotPasswordEmail(email);
        return true;
    }

    public void sendForgotPasswordEmail(String email){
        String forgotPasswordToken = tokenService.generateTempEmailToken(email);

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


    public void createAppUserAndAssignRole(AccountEntity account){
        AccountEntity savedAccount = accountRepository.save(account);
//        accountRoleService.assignRolesForUser(savedAccount.getId(), List.of(EnumRole.USER.getName()));
    }

    public boolean introspect(String token) {
        return tokenService.verifyToken(token);
    }
    // -----------------------------Utilities end-------------------------------
}
