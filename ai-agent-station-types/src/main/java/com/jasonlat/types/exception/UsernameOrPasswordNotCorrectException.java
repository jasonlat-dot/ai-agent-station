package com.jasonlat.types.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author jasonlat
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class UsernameOrPasswordNotCorrectException extends AppException  {
    public UsernameOrPasswordNotCorrectException(String info) {
        super(info);
    }

    public UsernameOrPasswordNotCorrectException(String code, Throwable cause) {
        super(code, cause);
    }

    public UsernameOrPasswordNotCorrectException(String code, String message) {
        super(code, message);
    }

    public UsernameOrPasswordNotCorrectException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}