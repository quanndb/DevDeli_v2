package com.example.identityService.application.service;

import com.devdeli.common.service.RedisService;
import com.example.identityService.application.util.TimeConverter;
import com.example.identityService.application.config.AuthenticationProperties;
import com.example.identityService.infrastructure.persistence.entity.AccountEntity;
import com.example.identityService.application.DTO.Token;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthenticationProperties.class)
public class TokenService implements InitializingBean {

    @Value(value = "${security.authentication.jwt.access-token-life-time}")
    private String ACCESS_TOKEN_LIFE_TIME;
    @Value(value = "${security.authentication.jwt.refresh-token-life-time}")
    private String REFRESH_TOKEN_LIFE_TIME;
    @Value(value = "${security.authentication.jwt.email-token-life-time}")
    private String EMAIL_TOKEN_LIFE_TIME;

    private KeyPair keyPair;
    private final AuthenticationProperties properties;
    private final RedisService redisService;
    private final AccountRoleService accountRoleService;

    @Override
    public void afterPropertiesSet() {
        this.keyPair = getKeyPair(properties.getKeyStore(),
                properties.getKeyStorePassword(),
                properties.getKeyAlias());
    }

    private KeyPair getKeyPair(String keyStore, String password, String alias){
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(keyStore), password.toCharArray());
        return keyStoreKeyFactory.getKeyPair(alias);
    }

    // token generators
    public String generateRefreshToken(String email, String ip){
        return otherTokenFactory(email, REFRESH_TOKEN_LIFE_TIME, ip);
    }

    public String generateTempEmailToken(String email, String ip){
        return otherTokenFactory(email, EMAIL_TOKEN_LIFE_TIME, ip);
    }

    public String accessTokenFactory(AccountEntity account) {

        String tokenId = UUID.randomUUID().toString();

        // build token
        return Jwts.builder()
                .subject(account.getEmail())
                .claim("email",account.getEmail())
                .claim("internal", account.getEmail())
                .issuer("DevDeli")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TimeConverter.convertToMilliseconds(ACCESS_TOKEN_LIFE_TIME)))
                .id(tokenId)
                .claim("scope", accountRoleService.getAllUserRole(account.getId()))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    public String otherTokenFactory(String email, String liveTime, String ip) {
        String tokenId = UUID.randomUUID().toString();
        // build token
        return Jwts.builder()
                .subject(email)
                .claim("email",email)
                .claim("internal", email)
                .issuer("DevDeli")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TimeConverter.convertToMilliseconds(liveTime)))
                .id(tokenId)
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .claim("IP", ip)
                .compact();
    }


    // utilities

    public JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) this.keyPair.getPublic()).keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(UUID.randomUUID().toString());
        return new JWKSet(builder.build());
    }

    public Jwt getTokenDecoded(String token){
       return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build().decode(token);
    }

    public boolean verifyToken(String token){
        return !isTokenExpired(token) && !isLogout(token);
    }

    public Claims extractClaims(String token){
        try{
            return Jwts.parser().verifyWith(keyPair.getPublic())
                    .build().parseSignedClaims(token).getPayload();
        }
        catch (ExpiredJwtException | SignatureException exception){
            return null;
        }
    }

    public boolean isTokenExpired(String token){
        try{
            Date expirationTime = JWTParser.parse(token).getJWTClaimsSet().getExpirationTime();
            return expirationTime != null && expirationTime.before(new Date());
        } catch (ParseException e) {
            return false;
        }

    }

    public boolean isLogout(String token){
        try{
            String tokenId = JWTParser.parse(token).getJWTClaimsSet().getJWTID();
            if(tokenId == null) return false;
            String valueOfLogoutToken = redisService.getValue("token_id:"+tokenId);
            return valueOfLogoutToken != null;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean deActiveToken(Token token) throws ParseException {
        String jwtid = JWTParser.parse(token.getValue()).getJWTClaimsSet().getJWTID();
        try{
            redisService
                    .putValue("token_id:"+jwtid,
                            "true",
                            Duration.ofMillis(token.getLifeTime()));
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
