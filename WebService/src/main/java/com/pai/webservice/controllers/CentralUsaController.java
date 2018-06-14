package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.*;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.repository.IMongoObjRepo;
import com.pai.webservice.repository.IMongoObjRepoUsa;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/centralUsability")
public class CentralUsaController {


    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IMongoObjRepoUsa mongoObjRepoUsa;


        @PostMapping(value = "")
        public @ResponseBody
        ResponseEntity<ResponseObject> processDialog(@Valid @RequestBody FrontObjUsaRequest input){

        if (StringUtils.isNotEmpty(input.getEffectiveness()) && StringUtils.isNotEmpty(input.getUsability())) {
            System.out.println("getEffectiveness: " + input.getEffectiveness());
            System.out.println("getUsability: " + input.getUsability());

        }

        Date date = new Date();

        MongoDbObjectUsa mongoDbObject = null;
        mongoDbObject = new MongoDbObjectUsa("USA"+ date.getTime(), input.getUsability(), input.getEffectiveness());
        System.out.println("USA"+ date.getTime());
        mongoObjRepoUsa.save(mongoDbObject);
        System.out.println("USA"+ date.getTime());
        return new ResponseEntity<ResponseObject>(new ResponseObject("OK", "Poszlo spoko :)"), HttpStatus.OK);

    }


}
