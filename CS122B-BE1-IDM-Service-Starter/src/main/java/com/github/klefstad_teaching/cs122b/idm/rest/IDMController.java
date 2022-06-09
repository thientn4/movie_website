package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import org.springframework.http.ResponseEntity;


import com.github.klefstad_teaching.cs122b.idm.model.RegisterLoginRequest;
import com.github.klefstad_teaching.cs122b.idm.model.RegisterAuthResponse;
import com.github.klefstad_teaching.cs122b.idm.model.LoginRefreshResponse;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.model.RefreshRequest;
import com.github.klefstad_teaching.cs122b.idm.model.AuthRequest;


@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterAuthResponse> registerEndpoint(@RequestBody RegisterLoginRequest request)
    {
        this.validate.validateEmail(request.getEmail());
        this.validate.validatePassword(request.getPassword());
        this.authManager.createAndInsertUser(request.getEmail(),request.getPassword());
        RegisterAuthResponse body=new RegisterAuthResponse();
        body.setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY);
        return ResponseEntity.status(body.getResult().status())
                .body(body);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRefreshResponse> loginEndpoint(@RequestBody RegisterLoginRequest request) throws JOSEException
    {
        this.validate.validateEmail(request.getEmail());
        this.validate.validatePassword(request.getPassword());
        User result_user=this.authManager.selectAndAuthenticateUser(request.getEmail(),request.getPassword());
        LoginRefreshResponse body=new LoginRefreshResponse();
        body.setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY);
        String new_access_token=jwtManager.buildAccessToken(result_user);
        body.setAccessToken(new_access_token);
        RefreshToken newRefreshToken=jwtManager.buildRefreshToken(result_user);
        this.authManager.insertRefreshToken(newRefreshToken);
        body.setRefreshToken(newRefreshToken.getToken());
        return ResponseEntity.status(body.getResult().status())
                .body(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginRefreshResponse> refreshEndpoint(@RequestBody RefreshRequest request) throws JOSEException
    {
        RefreshToken result=this.authManager.verifyRefreshToken(request.getRefreshToken());
        //update refreshToken.expireTime
        this.jwtManager.updateRefreshTokenExpireTime(result);
        LoginRefreshResponse body=new LoginRefreshResponse();
        if(result.getExpireTime().isAfter(result.getMaxLifeTime())){
            this.authManager.revokeRefreshToken(result);
            RefreshToken newRefreshToken=jwtManager.buildRefreshToken(this.authManager.getUserFromRefreshToken(result));
            this.authManager.insertRefreshToken(newRefreshToken);
            body.setRefreshToken(newRefreshToken.getToken());
            body.setAccessToken(this.jwtManager.buildAccessToken(this.authManager.getUserFromRefreshToken(result)));
        }else {
            this.jwtManager.updateRefreshTokenExpireTime(result);
            this.authManager.updateRefreshTokenExpireTime(result);
            body.setRefreshToken(result.getToken());
            body.setAccessToken(this.jwtManager.buildAccessToken(this.authManager.getUserFromRefreshToken(result)));
        }
        body.setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN);
        return ResponseEntity.status(body.getResult().status())
                .body(body);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<RegisterAuthResponse> authEndpoint(@RequestBody AuthRequest request)
    {
        this.jwtManager.verifyAccessToken(request.getAccessToken());
        RegisterAuthResponse body=new RegisterAuthResponse();
        body.setResult(IDMResults.ACCESS_TOKEN_IS_VALID);
        return ResponseEntity.status(body.getResult().status())
                .body(body);
    }
}
