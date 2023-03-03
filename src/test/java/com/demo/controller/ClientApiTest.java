package com.demo.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;

import com.demo.dto.ClientDto;
import com.demo.error.DataNotFoundException;
import com.demo.error.ValidationException;
import com.demo.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@SpringJUnitWebConfig
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ClientApiTest {
   static final Random RANDOM = new Random();
   static final ObjectMapper MAPPER = new ObjectMapper();
   
   @Autowired
   private MockMvc mockMvc;
   @MockBean
   private ClientService service;
   
   private Long id;
   private ClientDto client;
   
   @BeforeEach
   public void setup() {
      id = Math.abs(RANDOM.nextLong());
      client = new ClientDto();
      client.setClient(id);
      client.setFirstName("Dewald");
      client.setLastName("Pretorius");
      client.setMobileNumber(String.valueOf(Math.abs(RANDOM.nextLong())));
   }
   
   @Test
   void testFind() throws Exception {
      when(service.find(id)).thenReturn(client);

      mockMvc.perform(get("/v1/clients/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.client", is(id)))
            .andExpect(jsonPath("$.firstName", is("Dewald")))
            .andExpect(jsonPath("$.lastName", is("Pretorius")))
            .andExpect(jsonPath("$.mobileNumber", is(client.getMobileNumber())));
   }
   
   @Test
   void testNotFound() throws Exception {
      when(service.find(anyLong())).thenThrow(new DataNotFoundException("Test"));
      
      mockMvc.perform(get("/v1/clients/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Data not found -")));
   }

   @Test
   void testSearchByFirstName() throws Exception {
      String firstName = Long.toHexString(RANDOM.nextLong());
      String expectedResponse = MAPPER.writeValueAsString(List.of(client, client, client));
      
      when(service.search(null, firstName, null))
            .thenReturn(List.of(client, client, client));
      
      mockMvc.perform(get("/v1/clients")
               .queryParam("firstName", firstName)
            )
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
   }
   
   @Test
   void testSearchByIdNumber() throws Exception {
      String idNumber = Long.toHexString(RANDOM.nextLong());
      String expectedResponse = MAPPER.writeValueAsString(List.of(client, client, client));
      
      when(service.search(idNumber, null, null))
            .thenReturn(List.of(client, client, client));
      
      mockMvc.perform(get("/v1/clients")
               .queryParam("idNumber", idNumber)
            )
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
   }
   
   @Test
   void testSearchByMobileNumber() throws Exception {
      String mobileNumber = Long.toHexString(RANDOM.nextLong());
      String expectedResponse = MAPPER.writeValueAsString(List.of(client, client, client));
      
      when(service.search(null, null, mobileNumber))
            .thenReturn(List.of(client, client, client));
      
      mockMvc.perform(get("/v1/clients")
               .queryParam("mobileNumber", mobileNumber)
            )
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
   }

   @Test
   void testCreate() throws Exception {
      when(service.create(client)).thenReturn(id);

      mockMvc.perform(post("/v1/clients")
               .contentType(MediaType.APPLICATION_JSON)
               .content(MAPPER.writeValueAsString(client))
            )
            .andExpect(status().is(HttpStatus.CREATED.value()))
            .andExpect(content().string(id.toString()));
   }
   
   @Test
   void testCreateValidationFailure() throws Exception {
      List<String> reasons = List.of("Reason-1", "Reason-2", "Reason-3");
      when(service.create(client)).thenThrow(new ValidationException(reasons));

      mockMvc.perform(post("/v1/clients")
               .contentType(MediaType.APPLICATION_JSON)
               .content(MAPPER.writeValueAsString(client))
            )
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.reasons[0]", is("Reason-1")))
            .andExpect(jsonPath("$.reasons[1]", is("Reason-2")))
            .andExpect(jsonPath("$.reasons[2]", is("Reason-3")));
   }

   @Test
   void testUpdate() throws Exception {
      when(service.update(id, client)).thenReturn(client);
      
      mockMvc.perform(put("/v1/clients/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(MAPPER.writeValueAsString(client))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.client", is(id)))
            .andExpect(jsonPath("$.firstName", is("Dewald")))
            .andExpect(jsonPath("$.lastName", is("Pretorius")))
            .andExpect(jsonPath("$.mobileNumber", is(client.getMobileNumber())));
   }
   
   @Test
   void testUpdateNotFound() throws Exception {
      when(service.update(id, client)).thenThrow(new DataNotFoundException("Client id: " + id));
      
      mockMvc.perform(put("/v1/clients/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(MAPPER.writeValueAsString(client))
            )
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Data not found -")));
   }
   
   @Test
   void testUpdateValidationFailure() throws Exception {
      List<String> reasons = List.of("Reason-1", "Reason-2", "Reason-3");
      when(service.update(id, client)).thenThrow(new ValidationException(reasons));
      
      mockMvc.perform(put("/v1/clients/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(MAPPER.writeValueAsString(client))
            )
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.reasons[0]", is("Reason-1")))
            .andExpect(jsonPath("$.reasons[1]", is("Reason-2")))
            .andExpect(jsonPath("$.reasons[2]", is("Reason-3")));
   }

   @Test
   void testRemove() throws Exception {
      mockMvc.perform(delete("/v1/clients/{id}", id))
            .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
   }
   
   @Test
   void testRemoveNotFound() throws Exception {
      doThrow(new DataNotFoundException("Client id: " + id)).when(service).remove(id);
      
      mockMvc.perform(delete("/v1/clients/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Data not found -")));
   }
}
