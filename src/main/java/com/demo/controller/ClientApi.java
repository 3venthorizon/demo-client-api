package com.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.dto.ClientDto;
import com.demo.service.ClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
public class ClientApi {
   private final ClientService service;
   
   @GetMapping("/{id}")
   @ResponseBody
   public ClientDto find(@PathVariable("id") Long id) {
      return service.find(id);
   }
   
   @GetMapping
   @ResponseBody
   public List<ClientDto> search(@RequestParam(required = false) String idNumber, 
         @RequestParam(required = false) String firstName, 
         @RequestParam(required = false) String mobileNumber) {
      return service.search(idNumber, firstName, mobileNumber);
   }
   
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Long create(@RequestBody ClientDto client) {
      return service.create(client);
   }
   
   @PutMapping("/{id}")
   @ResponseBody
   public ClientDto update(@PathVariable("id") Long id, @RequestBody ClientDto client) {
      return service.update(id, client);
   }
   
   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public void remove(@PathVariable("id") Long id) {
      service.remove(id);
   }
}
