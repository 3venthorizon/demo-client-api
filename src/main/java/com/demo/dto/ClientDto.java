package com.demo.dto;

import lombok.Data;

@Data
public class ClientDto {
   private Long client;
   private String firstName;
   private String lastName;
   private String mobileNumber;
   private String idNumber;
   private String address;
}
