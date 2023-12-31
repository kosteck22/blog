package com.example.blog.exception;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record ApiError(
        String path,
        List<String> errors,
        int statusCode,
        Date timestamp
) {
}
