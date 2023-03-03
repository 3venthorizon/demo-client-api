package com.demo.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.demo.dto.ValidationFailureDto;

@ControllerAdvice
public class ErrorResponseHandler extends ResponseEntityExceptionHandler {

   @ExceptionHandler({ DataNotFoundException.class })
   protected ResponseEntity<Object> handleNotFound(DataNotFoundException error, WebRequest request) {
      String errorMessage = "Data not found - " + error.getMessage();
      return handleExceptionInternal(error, errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
   }
   
   @ExceptionHandler({ ValidationException.class })
   protected ResponseEntity<ValidationFailureDto> handleValidationError(ValidationException error) {
      return ResponseEntity.badRequest().body(new ValidationFailureDto(error.getReasons()));
   }
}
