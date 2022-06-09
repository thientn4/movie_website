package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.Date;
import java.time.Instant;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;


@Component
public class IDMJwtManager
{
    private final JWTManager jwtManager;

    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig)
    {
        this.jwtManager =
            new JWTManager.Builder()
                .keyFileName(serviceConfig.keyFileName())
                .accessTokenExpire(serviceConfig.accessTokenExpire())
                .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
                .refreshTokenExpire(serviceConfig.refreshTokenExpire())
                .build();
    }

    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
        throws JOSEException
    {
        JWSHeader header =
                new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                        .keyID(jwtManager.getEcKey().getKeyID())
                        .type(JWTManager.JWS_TYPE)
                        .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(jwtManager.getSigner());
        return signedJWT;
    }

    private void verifyJWT(SignedJWT jwt)
        throws JOSEException, BadJOSEException
    {
        jwt.verify(jwtManager.getVerifier());
        jwtManager.getJwtProcessor().process(jwt, null);
    }

    public String buildAccessToken(User user) throws JOSEException
    {
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getEmail())
                        .expirationTime(Date.from(
                                Instant.now().plus(this.jwtManager.getAccessTokenExpire())))
                        .claim(JWTManager.CLAIM_ID, user.getId())    // we set claims like values in a map
                        .claim(JWTManager.CLAIM_ROLES, user.getRoles())
                        .issueTime(Date.from(Instant.now()))
                        .build();
        SignedJWT signedJWT=buildAndSignJWT(claimsSet);
        return signedJWT.serialize();
    }

    public void verifyAccessToken(String jws)
    {
        try {
            SignedJWT rebuiltSignedJwt = SignedJWT.parse(jws);
            verifyJWT(rebuiltSignedJwt);
            if(Instant.now().isAfter(rebuiltSignedJwt.getJWTClaimsSet().getExpirationTime().toInstant())) {
                throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
            }
        }catch(java.text.ParseException | JOSEException | BadJOSEException e){
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
        }
    }

    public RefreshToken buildRefreshToken(User user)
    {
        RefreshToken result=new RefreshToken();
        result.setToken(generateUUID().toString());
        result.setTokenStatus(TokenStatus.fromId(1));

        result.setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()));
        result.setMaxLifeTime(Instant.now().plus(this.jwtManager.getMaxRefreshTokenLifeTime()));
        result.setUserId(user.getId());

        return result;
    }

    public boolean hasExpired(RefreshToken refreshToken)
    {
        return false;
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        return false;
    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
    {
        refreshToken.setExpireTime(Instant.now().plus(this.jwtManager.getRefreshTokenExpire()));
    }

    private UUID generateUUID()
    {
        return UUID.randomUUID();
    }
}
