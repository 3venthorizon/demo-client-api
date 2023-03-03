package com.demo.error;

public class DataNotFoundException extends RuntimeException {
   private static final long serialVersionUID = 1283034866060698014L;

   public DataNotFoundException() {
   }

   public DataNotFoundException(String message) {
      super(message);
   }

   public DataNotFoundException(Throwable cause) {
      super(cause);
   }

   public DataNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }

   public DataNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
