package com.example.identityService.application.service.auth;

import com.devdeli.common.dto.request.ClientTokenRequest;
import com.devdeli.common.dto.response.ClientTokenResponse;
import com.devdeli.common.service.RedisService;
import com.example.identityService.application.DTO.EmailEnum;
import com.example.identityService.application.DTO.request.ChangePasswordRequest;
import com.example.identityService.application.DTO.request.CreateAccountRequest;
import com.example.identityService.application.DTO.request.EmailRequest;
import com.example.identityService.application.DTO.request.LoginRequest;
import com.example.identityService.application.DTO.request.RegisterRequest;
import com.example.identityService.application.DTO.request.ResetPasswordRequest;
import com.example.identityService.application.DTO.response.GoogleUserResponse;
import com.example.identityService.application.DTO.response.LoginResponse;
import com.example.identityService.application.util.RandomCodeCreator;
import com.example.identityService.application.util.TimeConverter;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.infrastructure.persistence.entity.LogEntity;
import com.example.identityService.application.exception.AppExceptions;
import com.example.identityService.application.exception.ErrorCode;
import com.example.identityService.infrastructure.persistence.repository.AccountRepository;
import com.example.identityService.infrastructure.persistence.repository.LoggerRepository;
import com.example.identityService.application.service.EmailService;
import com.example.identityService.application.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAuthService{

    @Value(value = "${app.baseUrl}")
    private String APP_BASEURL;
    @Value(value = "${security.authentication.max-login-attempt}")
    private Integer MAX_LOGIN_ATTEMPT;
    @Value(value = "${security.authentication.login-delay-fail}")
    private String LOGIN_DELAY_FAIL;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LoggerRepository loggerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RedisService redisService;

    private static final List<AbstractAuthService> children = new ArrayList<>();

    public abstract LoginResponse performLogin(LoginRequest request);
    public abstract ClientTokenResponse performGetClientToken(ClientTokenRequest request);
    public abstract LoginResponse performLoginWithGoogle(String email, String password, String ip);
    public abstract boolean logout(String accessToken, String refreshToken) throws ParseException;
    public abstract Object getNewToken(String refreshToken);
    public abstract boolean performResetPassword(String token, String newPassword, String ip) throws ParseException;
    public abstract boolean performChangePassword(ChangePasswordRequest request, String ip);
    public abstract boolean performRegister(RegisterRequest request);
    public abstract boolean performCreateUser(CreateAccountRequest request);
    public abstract boolean performRegisterUserFromGoogle(AccountEntity request, String ip);

    public AbstractAuthService() {
        children.add(this);
    }

    // login
    public LoginResponse login(LoginRequest request){
        AccountEntity foundAccount = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
        boolean result = isValidUserStatus(foundAccount);
        if(result){
            AccountEntity account = accountRepository
                    .findByEmail(request.getEmail()).orElseThrow(()->new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
            boolean success = passwordEncoder.matches(request.getPassword(), account.getPassword());
            if(!success){
                String key = String.join("","login-attempt:", account.getEmail());
                String attemptTimeString =redisService.getValue(key);

                if(attemptTimeString != null && Integer.parseInt(attemptTimeString) == MAX_LOGIN_ATTEMPT)
                    throw new AppExceptions(ErrorCode.TOO_MUCH_LOGIN_FAIL);
                int value = attemptTimeString != null ? Integer.parseInt(attemptTimeString) + 1 : 1;
                redisService.putValue(key, Integer.toString(value),
                        Duration.ofMillis(TimeConverter.convertToMilliseconds(LOGIN_DELAY_FAIL)));
                throw new AppExceptions(ErrorCode.INVALID_EMAIL_PASSWORD);
            }

            boolean isNewIp = !loggerRepository.existsByEmailAndIp(account.getEmail(), request.getIp());
            if(isNewIp){
                sendConfirmValidIp(account.getEmail(), request.getIp());
            }
            loggerRepository.save(LogEntity.builder()
                    .actionName("LOGIN")
                    .email(account.getEmail())
                    .ip(request.getIp())
                    .build());

            return performLogin(request);
        }
        return null;
    }
    // google
    public LoginResponse loginWithGoogle(String code, String ip) {
        var token = googleAuthService.exchangeToken(code);
        var userResponse = googleAuthService.getUserInfo(token.getAccessToken());

        return accountRepository.findByEmail(userResponse.getEmail())
                .map(account -> performLoginWithGoogle(account.getEmail(), account.getPassword(), ip))
                .orElseGet(() -> {
                    AccountEntity newAccount = createAccountPattern(userResponse);
                    registerUserFromGoogle(newAccount, ip);
                    return performLoginWithGoogle(newAccount.getEmail(), newAccount.getPassword(), ip);
                });
    }

    // Function
    public static boolean register(RegisterRequest request) {
        for (AbstractAuthService child : children) {
            child.performRegister(request);
        }
        return true;
    }

    public static boolean createUser(CreateAccountRequest request) {
        for (AbstractAuthService child : children) {
            child.performCreateUser(request);
        }
        return true;
    }

    public static boolean changePassword(ChangePasswordRequest request, String ip){
        for (AbstractAuthService child : children) {
            child.performChangePassword(request, ip);
        }
        return true;
    }

    public static boolean resetPassword(ResetPasswordRequest request, String ip) throws ParseException {
        for (AbstractAuthService child : children) {
            child.performResetPassword(request.getToken(), request.getNewPassword(), ip);
        }
        return true;
    }

    public static boolean registerUserFromGoogle(AccountEntity request, String ip){
        for (AbstractAuthService child : children) {
            child.performRegisterUserFromGoogle(request, ip);
        }
        return true;
    }

    // utilities
    public static boolean isValidUserStatus(AccountEntity account){
        if(!account.isVerified()) throw new AppExceptions(ErrorCode.NOT_VERIFY_ACCOUNT);
        if(!account.isEnable()) throw new AppExceptions(ErrorCode.ACCOUNT_LOCKED);
        if(account.isDeleted()) throw new AppExceptions(ErrorCode.ACCOUNT_DELETED);
        return true;
    }

    public void sendVerifyEmail(String email, String ip){
        String verifyToken = tokenService.generateTempEmailToken(email, ip);
        String verifyUrl = String.join("",APP_BASEURL,"auth/verification?token=",verifyToken);
        emailService
                .sendEmail(new EmailRequest(EmailEnum.VERIFY_EMAIL.getSubject(),
                        String.join(" ",EmailEnum.VERIFY_EMAIL.getContent(), verifyUrl)
                        , List.of(email)));
    }

    public void sendConfirmValidIp(String email, String ip){
        String verifyToken = tokenService.generateTempEmailToken(email,ip);
        String verifyUrl = String.join("",APP_BASEURL,"auth/verification?token=",verifyToken);
        emailService
                .sendEmail(new EmailRequest(EmailEnum.CONFIRM_IP.getSubject(),
                        String.join(" ",EmailEnum.CONFIRM_IP.getContent(), verifyUrl)
                        ,List.of(email)));
    }

    public void sendResetPasswordSuccess(String email){
        emailService
                .sendEmail(new EmailRequest(EmailEnum.RESET_PASSWORD_SUCCESS.getSubject(),
                        String.join(" ",EmailEnum.RESET_PASSWORD_SUCCESS.getContent(), LocalDateTime.now().toString())
                        ,List.of(email)));
    }

    private AccountEntity createAccountPattern(GoogleUserResponse userResponse) {
        return AccountEntity.builder()
                .email(userResponse.getEmail())
                .password(passwordEncoder.encode(RandomCodeCreator.generateCode() + ""))
                .fullname(userResponse.getName())
                .cloudImageUrl(userResponse.getPicture())
                .verified(userResponse.isEmailVerified())
                .enable(true)
                .deleted(false)
                .build();
    }
}

