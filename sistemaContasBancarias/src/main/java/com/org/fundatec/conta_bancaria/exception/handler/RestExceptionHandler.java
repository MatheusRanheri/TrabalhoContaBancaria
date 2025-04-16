package com.org.fundatec.conta_bancaria.exception.handler;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> trataRegistroNaoEcontradoException(RegistroNaoEncontradoException ex){
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex ){
      StringBuilder sb = new StringBuilder(50);
      ex.getBindingResult().getAllErrors().forEach( error ->{
        String fieldName =((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        sb.append(fieldName);
        sb.append(" - ");
        sb.append(errorMessage);
        sb.append("; ");
      });
        ErroResponse errorResponse = new ErroResponse(HttpStatus.BAD_REQUEST.value(), sb.toString());
        return new ResponseEntity<ErroResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
