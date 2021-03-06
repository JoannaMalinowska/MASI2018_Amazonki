package com.pai.webservice.controllers;

import am.ik.aws.apa.jaxws.ItemSearchResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.service.AmazonResponseService;
import com.pai.webservice.service.AmazonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/amazon")
public class AmazonController {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private AmazonResponseService amazonResponseService;

    @PostMapping(value = "")
    public @ResponseBody
    ResponseEntity getResult(@Valid @RequestBody List<String> keywords) {

        amazonService.setSearchCategory("Books");
        amazonService.prepareKeywordsForRequest(keywords);

        amazonResponseService.setAmazonResponse(amazonService.getResultFromRequest());

        JsonNode returnData = mapper.valueToTree(amazonResponseService.getLinkWithFinalResults());
        return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
    }

}
