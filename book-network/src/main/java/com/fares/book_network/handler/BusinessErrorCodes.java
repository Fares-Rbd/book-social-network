package com.fares.book_network.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

public enum BusinessErrorCodes {


    NO_CODE(0, "No code", NOT_IMPLEMENTED),
    INCORRECT_CURRENT_PASSWORD(300, "Incorrect Current Password", BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(301, " New Password Does Not Match", BAD_REQUEST),
    ACCOUNT_LOCKED(302,"User Account is Locked",FORBIDDEN),
    ACCOUNT_DISABLED(303,"User Account is Disabled",FORBIDDEN),
    BAD_CREDENTIALS(304,"Username and/or Password is Incorrect",FORBIDDEN),
    ;

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
