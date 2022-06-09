package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;


@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    public final IDMRepo repo;

    @Autowired
    public IDMAuthenticationManager(IDMRepo repo)
    {
        this.repo = repo;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    public User selectAndAuthenticateUser(String email, char[] password)
    {
        User result=this.repo.getUserFromEmail(email);
        if(result==null)
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        if(result.getUserStatus().id()==2)
            throw new ResultError(IDMResults.USER_IS_LOCKED);
        if(result.getUserStatus().id()==3)
            throw new ResultError(IDMResults.USER_IS_BANNED);
        if(!result.getHashedPassword().equals(
                Base64.getEncoder().encodeToString(hashPassword(password,result.getSalt()))
        ))
            throw new ResultError(IDMResults.INVALID_CREDENTIALS);

        return result;
    }

    public void createAndInsertUser(String email, char[] password)
    {
        int newStatusId=1;
        byte[] earlySalt=genSalt();
        String newSalt=Base64.getEncoder().encodeToString(earlySalt);
        byte[] earlyHashPass=hashPassword(password,earlySalt);
        String newHashPass=Base64.getEncoder().encodeToString(earlyHashPass);

        try {
            this.repo.addUser(email,newStatusId,newSalt,newHashPass);
        }catch(Exception e){
            throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
        }
    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        this.repo.addRefreshToken(
                refreshToken.getToken(),
                refreshToken.getUserId(),
                refreshToken.getTokenStatus().id(),
                refreshToken.getExpireTime(),
                refreshToken.getMaxLifeTime());
    }

    public RefreshToken verifyRefreshToken(String token)
    {
        if(token.length()!=36)
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_LENGTH);
        try{
            UUID uuid = UUID.fromString(token);
        } catch (IllegalArgumentException exception){
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_FORMAT);
        }
        RefreshToken result=this.repo.getRefreshToken(token);
        if(result==null) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }
        if(result.getTokenStatus().id()==2) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }
        if(result.getTokenStatus().id()==3) {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        }
        if(Instant.now().isAfter(result.getExpireTime())||
                Instant.now().isAfter(result.getMaxLifeTime())) {
            expireRefreshToken(result);
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }
        return result;
    }

    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
        this.repo.updateRefreshToken(token);
    }

    public void expireRefreshToken(RefreshToken token)
    {
        token.setTokenStatus(TokenStatus.fromId(2));
        this.repo.updateRefreshToken(token);
    }

    public void revokeRefreshToken(RefreshToken token)
    {
        token.setTokenStatus(TokenStatus.fromId(3));
        this.repo.updateRefreshToken(token);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {
        return this.repo.getUserFromId(refreshToken.getId());
    }
}
