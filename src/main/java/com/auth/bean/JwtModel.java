package com.auth.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtModel {

    private Long UserTokenId;
    private Long UserId;
    private String token;
    private Date issueTime;
    private Date expirationTime;
    private boolean valid;
    private String CreatedBy;
    private Date DateCreated;
    private String ModifyBy;
    private Date DateModify;
    
    public boolean isValid() {
        return valid;
    }
    public void setValid(final boolean valid) {
        this.valid = valid;
    }
}
