package com.pai.webservice.exception;

import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject> invalidInput(MethodArgumentNotValidException ex) {

        return new ResponseEntity<>(ResponseObject.createError(Notification.INVALID_MODEL_STRUCTURE), HttpStatus.BAD_REQUEST);
    }
}
