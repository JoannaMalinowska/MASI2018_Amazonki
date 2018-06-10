package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.assistant.v1.model.SystemResponse;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.model.WatsonAssistantObject;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.repository.IMongoObjRepo;
import com.pai.webservice.service.CentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/central")
public class CentralController {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private Environment env;

    @Autowired
    private IMongoObjRepo mongoObjRepo;

    @Autowired
    private CentralService centralService;

    private WatsonAssistantObject watsonAssistantObject;

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
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange(env.getProperty("amazonResultURL.path"), HttpMethod.POST, entityAmazon, ResponseObject.class);
                JsonNode returnData = mapper.valueToTree(amazonResponse.getBody().getData());
                return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
            }
        }

        HttpEntity<FrontObj> entityWatsonAss = new HttpEntity<FrontObj>(input, headers);

        try {
            watsonAssistantObject = this.centralService.getWatsonAssistantResponse(input);
        } catch (Exception ex){
            FrontObj frontObj = new FrontObj("Chatbot error. Try again for next 20 minutes.", "-1");
            JsonNode returnData = mapper.valueToTree(frontObj);
            return new ResponseEntity<ResponseObject>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
        }

        //welcome
        if (watsonAssistantObject.getAssistantData().size() > 0 && watsonAssistantObject.getAssistantData().get("watsonData") !=null
                && watsonAssistantObject.getAssistantData().get("watsonData").asText().contains("&") && watsonAssistantObject.getAssistantData().get("watsonData").asText().split("&")[1].equals("W")) {

            MongoDbObject mongoDbObject = this.centralService.prepareMongoObjInWelcome(isNew, watsonAssistantObject.getConvId(), watsonAssistantObject.getContext());
            mongoObjRepo.save(mongoDbObject);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,
                    mapper.valueToTree(this.centralService.createWatsonResponse(watsonAssistantObject.getAssistantData(), watsonAssistantObject.getConvId()))), HttpStatus.OK);
        }

        //dialog
        if (watsonAssistantObject.getAssistantData().size() > 0 && watsonAssistantObject.getAssistantData().get("watsonData") !=null
                && watsonAssistantObject.getAssistantData().get("watsonData").asText().contains("&") && watsonAssistantObject.getAssistantData().get("watsonData").asText().split("&")[1].equals("T")) {

            List<String> keywords = this.centralService.getKeywordsFromWatson(input);
            MongoDbObject mongoDbObject = this.centralService.prepareMongoObjInDialog(isNew, keywords, watsonAssistantObject.getConvId(), watsonAssistantObject.getContext());
            mongoObjRepo.save(mongoDbObject);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,
                    mapper.valueToTree(this.centralService.createWatsonResponse(watsonAssistantObject.getAssistantData(), watsonAssistantObject.getConvId()))), HttpStatus.OK);
        }


        //end conversation
        if(watsonAssistantObject.getAssistantData().size() > 0 && watsonAssistantObject.getAssistantData().get("watsonData") !=null
                && watsonAssistantObject.getAssistantData().get("watsonData").asText().contains("&") && watsonAssistantObject.getAssistantData().get("watsonData").asText().split("&")[1].equals("K")){

            MongoDbObject mongoDbObject = null;
            List<String> resultKeywords = null;
            if(isNew){
                JsonNode returnData = mapper.valueToTree("Incorrect");

                return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
            }
            else{
                if(this.centralService.checkEndConversation(watsonAssistantObject.getConvId()) ){
                    return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, mapper.valueToTree("Incorrect")), HttpStatus.OK);
                }
                List<String> keywords = this.centralService.getKeywordsFromWatson(input);
                mongoDbObject = this.centralService.prepareMongoObjForEnding(isNew, keywords, watsonAssistantObject.getConvId(), watsonAssistantObject.getContext());
                mongoObjRepo.save(mongoDbObject);
                resultKeywords = mongoDbObject.getKeywords();
            }
            HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(resultKeywords, headers);
            ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange(env.getProperty("amazonResultURL.path"), HttpMethod.POST, entityAmazon, ResponseObject.class);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,
                    mapper.valueToTree(this.centralService.createAmazonResponse(amazonResponse, watsonAssistantObject.getConvId()))), HttpStatus.OK);
        }

        //misunderstanding
        else {
            MongoDbObject mongoDbObject = this.centralService.prepareMongoObjForMisunderstanding(isNew, watsonAssistantObject.getConvId(), watsonAssistantObject.getContext());

            mongoObjRepo.save(mongoDbObject);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,
                    mapper.valueToTree(this.centralService.createWatsonResponse(watsonAssistantObject.getAssistantData(), watsonAssistantObject.getConvId()))), HttpStatus.OK);
        }


    }

}
