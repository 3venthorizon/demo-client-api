package com.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.demo.dto.ClientDto;
import com.demo.error.DataNotFoundException;
import com.demo.error.ValidationException;
import com.demo.repository.ClientRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {
   private final ClientRepo clientRepo;

   public ClientDto find(Long id) {
      ClientDto client = clientRepo.findById(id);
      
      if (client == null) {
         throw new DataNotFoundException("Client id: " + id);
      }
      
      return client;
   }

   public Long create(ClientDto client) {
      validateNonTransient(client);
      String mobileNumber = client.getMobileNumber();
      
      //check transient errors - error which may not occur in subsequent requests when the data changes.
      if (mobileNumber != null && clientRepo.fieldExists(ClientDto::getMobileNumber, mobileNumber)) {
         throw new ValidationException("Client creation failed: Existing mobileNumber");
      }
      if (clientRepo.fieldExists(ClientDto::getIdNumber, client.getIdNumber())) {
         throw new ValidationException("Client creation failed: Existing idNumber");
      }
      
      return clientRepo.insert(client);
   }

   public void remove(Long id) {
      if (!clientRepo.delete(id)) {
         throw new DataNotFoundException("Client removal failed for id: " + id);
      }
   }

   public ClientDto update(Long id, ClientDto client) {
      validateNonTransient(client);
      ClientDto oldClient = find(id);
      String mobileNumber = client.getMobileNumber();
      
      //check transient errors - error which may not occur in subsequent requests when the data changes.
      if (!Objects.equals(oldClient.getMobileNumber(), mobileNumber)
            && mobileNumber != null && clientRepo.fieldExists(ClientDto::getMobileNumber, mobileNumber)) {
         throw new ValidationException("Client update failed: Existing mobileNumber");
      }
      if (!Objects.equals(oldClient.getIdNumber(), client.getIdNumber())
            && clientRepo.fieldExists(ClientDto::getIdNumber, client.getIdNumber())) {
         throw new ValidationException("Client creation failed: Existing idNumber");
      }
      
      client.setClient(id);
      clientRepo.update(id, client);
      return client;
   }

   public List<ClientDto> search(String idNumber, String firstName, String mobileNumber) {
      return clientRepo.search(idNumber, firstName, mobileNumber);
   }
   
   void validateNonTransient(ClientDto client) {
      List<String> reasons = new ArrayList<>();
      
      if (client.getFirstName() == null) {
         reasons.add("Mandatory firstName is not submitted");
      }
      if (client.getLastName() == null) {
         reasons.add("Mandatory lastName is not submitted");
      }
      if (client.getIdNumber() == null) {
         reasons.add("Mandatory idNumber is not submitted");
      } else if (!validateIdNumber(client.getIdNumber())) {
         reasons.add("Invalid idNumber: " + client.getIdNumber());
      }
      if (!validateMobileNumber(client.getMobileNumber())) {
         reasons.add("Invalid mobileNumber: " + client.getMobileNumber());
      }
      
      if (!reasons.isEmpty()) {
         throw new ValidationException(reasons);
      }
   }
   
   boolean validateIdNumber(String idNumber) {
      if (idNumber == null || idNumber.length() < 13) return false;
      
      int checksum = 0;
      
      for (int index = 12; index >= 0; index--) {
         int digit = Character.getNumericValue(idNumber.charAt(index));

         if ((index % 2) == 0) {
            checksum += digit;
         } else {
            checksum += digit < 5 ? digit * 2 : digit * 2 - 9;
         }
      }
      
      return (checksum % 10) == 0;
   }
   
   boolean validateMobileNumber(String mobileNumber) {
      if (mobileNumber == null) return true;
      
      try {
         Long.parseLong(mobileNumber);
         return true;
      } catch (Exception e) {
      }
      
      return false;
   }
}
