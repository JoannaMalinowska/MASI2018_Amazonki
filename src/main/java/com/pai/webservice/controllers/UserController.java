package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static ObjectMapper mapper = new ObjectMapper();

    @GetMapping(value = "/{name}")
    public @ResponseBody
    ResponseEntity getUserByName(@PathVariable("name") String name) {

        String caption = "Hello " + name;
        JsonNode returnData = mapper.valueToTree(caption);
        return new ResponseEntity<>(ResponseObject.createSuccess(Notification.USER_GET_SUCCESS, returnData), HttpStatus.OK);
    }

}
