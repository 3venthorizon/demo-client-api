package com.demo.error;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ValidationException extends RuntimeException {
   private static final long serialVersionUID = -324080258182296425L;
   
   @Getter
   final List<String> reasons = new ArrayList<>();

   public ValidationException() {
   }

   public ValidationException(String message) {
      super(message);
      reasons.add(message);
   }

   public ValidationException(Throwable cause) {
      super(cause);
   }

   public ValidationException(String message, Throwable cause) {
      super(message, cause);
      reasons.add(message);
   }

   public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
      reasons.add(message);
   }

   public ValidationException(List<String> reasons) {
      this.reasons.addAll(reasons);
   }
}
