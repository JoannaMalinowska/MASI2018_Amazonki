package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/central")
public class CentralController {

    private static ObjectMapper mapper = new ObjectMapper();

    @PostMapping(value = "")
    public @ResponseBody
    ResponseEntity processDialog(@Valid @RequestBody String inputText) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entityWatson = new HttpEntity<String>(inputText, headers);
        ResponseEntity<ResponseObject> watsonResponse = restTemplate.exchange("http://localhost:8080/api/watson", HttpMethod.POST,entityWatson,ResponseObject.class);

        JsonNode data = watsonResponse.getBody().getData();
        List<String> keywords = new ArrayList<>();

        for(int i=0; i< data.size(); i++){
            JsonNode keyword = data.get(i);
            keywords.add(keyword.get("text").asText());
        }

        HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(keywords, headers);
        ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon", HttpMethod.POST,entityAmazon,ResponseObject.class);


        JsonNode returnData = mapper.valueToTree("Halo");
        return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
    }

}
