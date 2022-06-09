package com.github.klefstad_teaching.cs122b.idm.util;

import org.springframework.stereotype.Component;

//Thien's import
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public final class Validate
{
    public void validateEmail(String email){
        if(email.length()<6 || email.length()>32)
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches())
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
    }

    public void validatePassword(char[] password){
        if(password.length<10||password.length>20)
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);

        boolean hasUpper=false;
        boolean hasLower=false;
        boolean hasNum=false;
        for(int i=0; i< password.length; i++){
            if(password[i]<='Z'&&password[i]>='A')
                hasUpper=true;
            else if(password[i]<='z'&&password[i]>='a')
                hasLower=true;
            else if(password[i]<='9'&&password[i]>='0')
                hasNum=true;
            else
                throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }

        if(!hasUpper || !hasLower || !hasNum)
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
    }
}
