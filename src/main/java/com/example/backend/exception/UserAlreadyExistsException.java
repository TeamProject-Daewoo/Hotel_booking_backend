package com.example.backend.exception;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final String loginType;

    public UserAlreadyExistsException(String message, String loginType) {
        super(message);
        this.loginType = loginType;
    }
}