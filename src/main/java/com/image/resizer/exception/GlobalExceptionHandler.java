package com.image.resizer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> serverError(Exception e) {
        log.error("[Exception 발생] message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body("서버 오류");
    }

    @ExceptionHandler({UnSupportExtensionException.class, MaxUploadSizeExceededException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> badRequest(Exception e) {
        log.error("[{} 발생] message: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }
}
