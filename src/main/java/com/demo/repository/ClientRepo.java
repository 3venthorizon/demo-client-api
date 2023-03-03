package com.demo.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.demo.dto.ClientDto;

/**
 * In-memory client storage used purely for illustration purposes.
 */
@Component
public class ClientRepo {
   private final Map<Long, ClientDto> clientMap = new HashMap<>();

   public boolean delete(Long id) {
      ClientDto client = clientMap.remove(id);
      return client != null;
   }

   public ClientDto findById(Long id) {
      return clientMap.get(id);
   }

   public void update(Long id, ClientDto client) {
      clientMap.put(id, client);
   }

   public Long insert(ClientDto client) {
      Long id = nextId();
      client.setClient(id);
      clientMap.put(id, client);
      return id;
   }
   
   public List<ClientDto> search(String idNumber, String firstName, String mobileNumber) {
      List<ClientDto> resultList = new ArrayList<>();
      
      for (ClientDto client : clientMap.values()) {
         if (Objects.equals(idNumber, client.getIdNumber())
               || Objects.equals(firstName, client.getFirstName())
               || (mobileNumber != null && Objects.equals(mobileNumber, client.getMobileNumber()))) {
            resultList.add(client);
         }
      }
      
      return resultList;
   }
   
   public <T> boolean fieldExists(Function<ClientDto, T> field, T value) {
      return clientMap.values().stream().map(field).filter(Objects::nonNull).anyMatch(value::equals);
   }

   Long nextId() {
      return clientMap.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1L;
   }
}
