package com.nhnacademy.associationAPI.error;

import com.nhnacademy.associationAPI.exception.LoginFailedException;
import com.nhnacademy.associationAPI.exception.UserAlreadyExistsException;
import com.nhnacademy.associationAPI.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            LoginFailedException.class,
            UserAlreadyExistsException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleValidException(Exception ex, HttpServletRequest request){
        int status;
        String message;
        String path = request.getRequestURI();

        if(ex instanceof MethodArgumentNotValidException ||
            ex instanceof IllegalArgumentException){
            status = 400;
            message = "입력 형식 오류";
        }else if(ex instanceof LoginFailedException){
            status = 401;
            message = ex.getMessage();
        }else if(ex instanceof UserAlreadyExistsException){
            status = 409;
            message = ex.getMessage();
        }else{
            status = 404;
            message = ex.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(status, message, path);

        return ResponseEntity.status(status).body(errorResponse);
    }
}
