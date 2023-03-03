package com.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.demo.dto.ClientDto;
import com.demo.error.DataNotFoundException;
import com.demo.error.ValidationException;
import com.demo.repository.ClientRepo;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
   static final Random RANDOM = new Random();
   
   @Mock
   private ClientRepo repo;
   @InjectMocks
   private ClientService service;

   @Test
   void testValidateIdNumber() {
      assertFalse(service.validateIdNumber(null)); //n/a
      assertFalse(service.validateIdNumber("960710480008")); //too short
      //invalid ids
      assertFalse(service.validateIdNumber("9607104800081")); //female 
      assertFalse(service.validateIdNumber("7711145800089")); //male
      //valid ids
      assertTrue(service.validateIdNumber("9607104800084")); //female 
      assertTrue(service.validateIdNumber("7711145800087")); //male
   }
   
   @Test
   void testValidateMobileNumber() {
      assertTrue(service.validateMobileNumber("7711145800089"));
      assertFalse(service.validateMobileNumber("77111ABC00089"));
   }
   
   @Test
   void testValidateNonTransient() {
      ClientDto client = new ClientDto(); //empty client
      
      ValidationException error = 
            assertThrows(ValidationException.class, () -> service.validateNonTransient(client));
      assertTrue(error.getReasons().stream().anyMatch(reason -> reason.contains("Mandatory firstName is not submitted")));
      assertTrue(error.getReasons().stream().anyMatch(reason -> reason.contains("Mandatory lastName is not submitted")));
      assertTrue(error.getReasons().stream().anyMatch(reason -> reason.contains("Mandatory idNumber is not submitted")));
      
      client.setIdNumber("bad id number");
      client.setMobileNumber("no dial tone");
      
      ValidationException invalidError = 
            assertThrows(ValidationException.class, () -> service.validateNonTransient(client));
      assertFalse(invalidError.getReasons().stream().anyMatch(reason -> reason.contains("Mandatory idNumber is not submitted")));
      assertTrue(invalidError.getReasons().stream().anyMatch(reason -> reason.contains("Invalid idNumber: ")));
      assertTrue(invalidError.getReasons().stream().anyMatch(reason -> reason.contains("Invalid mobileNumber: ")));
   }
   
   @Test
   void testCreate() {
      ClientService spy = spy(service);
      ClientDto client = new ClientDto();
      Long id = RANDOM.nextLong();
      
      client.setIdNumber("7711145800087"); 
      client.setMobileNumber("7711145800089");
      
      doNothing().when(spy).validateNonTransient(client);
      when(repo.fieldExists(any(Function.class), eq("7711145800087"))).thenReturn(Boolean.FALSE);
      when(repo.fieldExists(any(Function.class), eq("7711145800089"))).thenReturn(Boolean.FALSE);
      when(repo.insert(client)).thenReturn(id);
      
      assertEquals(id, spy.create(client));
      
      verify(spy).validateNonTransient(client);
   }
   
   @Test
   void testCreateExistingIdNumber() {
      ClientService spy = spy(service);
      ClientDto client = new ClientDto();
      
      client.setIdNumber("7711145800087"); 
      client.setMobileNumber("7711145800089");
      
      doNothing().when(spy).validateNonTransient(client);
      when(repo.fieldExists(any(Function.class), eq("7711145800089"))).thenReturn(Boolean.FALSE);
      when(repo.fieldExists(any(Function.class), eq("7711145800087"))).thenReturn(Boolean.TRUE);
      
      assertThrows(ValidationException.class, () -> spy.create(client));
      
      verify(spy).validateNonTransient(client);
   }
   
   @Test
   void testCreateExistingMobileNumber() {
      ClientService spy = spy(service);
      ClientDto client = new ClientDto();
      
      client.setIdNumber("7711145800087"); 
      client.setMobileNumber("7711145800089");
      
      doNothing().when(spy).validateNonTransient(client);
      when(repo.fieldExists(any(Function.class), eq("7711145800089"))).thenReturn(Boolean.TRUE);
      
      assertThrows(ValidationException.class, () -> spy.create(client));
      
      verify(spy).validateNonTransient(client);
   }
   
   @Test
   void testUpdate() {
      Long id = RANDOM.nextLong();
      ClientService spy = spy(service);
      ClientDto client = new ClientDto();
      ClientDto oldClient = new ClientDto();
      
      client.setIdNumber("7711145800087"); 
      client.setMobileNumber("7711145800089");
      oldClient.setIdNumber("7711145800089"); 
      oldClient.setMobileNumber("7711145800087");
      
      doNothing().when(spy).validateNonTransient(client);
      doReturn(oldClient).when(spy).find(id);
      when(repo.fieldExists(any(Function.class), eq("7711145800087"))).thenReturn(Boolean.FALSE);
      when(repo.fieldExists(any(Function.class), eq("7711145800089"))).thenReturn(Boolean.FALSE);
      
      assertEquals(client, spy.update(id, client));
      
      verify(spy).validateNonTransient(client);
   }
   
   @Test
   void testFind() {
      Long id = RANDOM.nextLong();
      ClientDto client = new ClientDto();
      
      when(repo.findById(id))
            .thenReturn(client)
            .thenReturn(null); //not found
      
      assertEquals(client, service.find(id));
      assertThrows(DataNotFoundException.class, () -> service.find(id));
   }
   
   @Test
   void testRemove() {
      Long id = RANDOM.nextLong();
      
      when(repo.delete(id))
            .thenReturn(true)
            .thenReturn(false);
      
      service.remove(id);
      assertThrows(DataNotFoundException.class, () -> service.remove(id));
   }
}
