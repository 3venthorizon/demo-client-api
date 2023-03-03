# demo-client-api

Build with maven:

    mvn clean install
  
There is also a swagger-ui to test with: http://localhost:8080/swagger-ui/#/

REST API that allows for creating, updating and searching for a client.

A client should have the following fields, fields marked with * a mandatory

  Client
  First Name*
  Last Name*
  Mobile Number
  ID Number*
  Physical Address

When a client is created or updated the following fields should be validate

ID Number

    Must be a valid south African ID number
    No Duplicates ID numbers

Mobile Number

    No duplicate mobile numbers

When validation fails an appropriate response should be provided.

 
You should be able to search for a client using any one of the following fields FirstName or ID Number or Phone Number

The data created does not have to be persisted to any database.

