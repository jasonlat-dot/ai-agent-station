package com.jasonlat.types.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author jasonlat
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class AccountExistException extends AppException {

    public AccountExistException(String info) {
        super(info);
    }

    public AccountExistException(String code, Throwable cause) {
        super(code, cause);
    }

    public AccountExistException(String code, String message) {
        super(code, message);
    }

    public AccountExistException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}