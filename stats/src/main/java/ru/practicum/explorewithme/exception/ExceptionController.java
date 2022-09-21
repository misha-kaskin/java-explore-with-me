package ru.practicum.explorewithme.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(NotFoundException e) {
        return ApiError.builder()
                .errors(new ArrayList<>())
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .reason(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
