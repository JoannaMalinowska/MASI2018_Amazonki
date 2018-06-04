package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.repository.IMongoObjRepo;
import com.pai.webservice.service.CentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/central")
public class CentralController {


    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IMongoObjRepo mongoObjRepo;

    @Autowired
    private CentralService centralService;

    @PostMapping(value = "")
    public @ResponseBody
    ResponseEntity processDialog(@Valid @RequestBody FrontObj input) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        boolean isNew = false;

        if(input.getCon_id() != null && input.getCon_id().equals("-1")){
            isNew = true;
        }
        else {
            List<MongoDbObject> list = this.mongoObjRepo.findAllByConvId(input.getCon_id());
            Collections.sort(list);
            MongoDbObject first = list.get(0);
            if(first.getQuestions() >= 8){ // condition for 20 result
                HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(first.getKeywords(), headers);
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon", HttpMethod.POST, entityAmazon, ResponseObject.class);
                JsonNode returnData = mapper.valueToTree(amazonResponse.getBody().getData());

                return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
            }
        }


        HttpEntity<FrontObj> entityWatsonAss = new HttpEntity<FrontObj>(input, headers);
        System.out.println(entityWatsonAss);

        ResponseEntity<ResponseObject> watsonAssResponse;
        JsonNode assData;
        String convId;
        try {
            watsonAssResponse = restTemplate.exchange("http://localhost:8080/api/watsonAst", HttpMethod.POST,entityWatsonAss,ResponseObject.class);

            ResponseObject watsonResponse =  watsonAssResponse.getBody();
            convId = watsonResponse.getData().get("con_id").asText();
            assData = watsonResponse.getData().get("assistantAnswer");


        } catch (Exception ex){
            FrontObj frontObj = new FrontObj("Chatbot error. Try again for next 20 minutes.", "-1");
            JsonNode returnData = mapper.valueToTree(frontObj);
            return new ResponseEntity<ResponseObject>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
        }


        //welcome
        if (assData.size() > 0 && assData.get("watsonData") !=null && assData.get("watsonData").asText().contains("&") && assData.get("watsonData").asText().split("&")[1].equals("W")) {

            MongoDbObject mongoDbObject = this.centralService.prepareMongoObjInWelcome(isNew, convId);
            mongoObjRepo.save(mongoDbObject);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, mapper.valueToTree(this.centralService.createWatsonResponse(assData, convId))), HttpStatus.OK);
        }

        //dialog
        if (assData.size() > 0 && assData.get("watsonData") !=null && assData.get("watsonData").asText().contains("&") && assData.get("watsonData").asText().split("&")[1].equals("T")) {

            List<String> keywords = this.centralService.getKeywordsFromWatson(input);
            MongoDbObject mongoDbObject = this.centralService.prepareMongoObjInDialog(isNew, keywords, convId);
            mongoObjRepo.save(mongoDbObject);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, mapper.valueToTree(this.centralService.createWatsonResponse(assData, convId))), HttpStatus.OK);
        }


        //end conversation
        if(assData.size() > 0 && assData.get("watsonData") !=null && assData.get("watsonData").asText().contains("&") && assData.get("watsonData").asText().split("&")[1].equals("K")){

            MongoDbObject mongoDbObject = null;
            List<String> resultKeywords = null;
            if(isNew){
                JsonNode returnData = mapper.valueToTree("Incorrect");

                return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
            }
            else{
                MongoDbObject first = this.centralService.getLastObjectFromMongo(convId);

                resultKeywords = first.getKeywords();
                if(resultKeywords.size()< 1 ){
                    JsonNode returnData = mapper.valueToTree("Incorrect");

                    return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
                }
            }
            HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(resultKeywords, headers);
            ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon", HttpMethod.POST, entityAmazon, ResponseObject.class);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, mapper.valueToTree(this.centralService.createAmazonResponse(amazonResponse, convId))), HttpStatus.OK);
        }

        //misunderstanding
        else {
            MongoDbObject mongoDbObject = this.centralService.prepareMongoObjForMisunderstanding(isNew, convId);

            mongoObjRepo.save(mongoDbObject);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, mapper.valueToTree(this.centralService.createWatsonResponse(assData, convId))), HttpStatus.OK);
        }


    }

}
