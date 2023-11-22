package com.example.blog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class CustomAuthorizationException extends RuntimeException {
    public CustomAuthorizationException(String msg) {
        super(msg);
    }
}
