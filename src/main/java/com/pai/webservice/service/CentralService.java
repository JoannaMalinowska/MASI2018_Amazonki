package com.pai.webservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.watson.developer_cloud.assistant.v1.model.SystemResponse;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.repository.IMongoObjRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CentralService implements ICentralService {

    @Autowired
    private IMongoObjRepo mongoObjRepo;

    public int countWords(String s){

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
    }

    @Override
    public List<String> getKeywordsFromWatson(FrontObj input) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entityWatson = new HttpEntity<String>(input.getText(), headers);
        ResponseEntity<ResponseObject> watsonResponse = restTemplate.exchange("http://localhost:8080/api/watson", HttpMethod.POST, entityWatson, ResponseObject.class);

        JsonNode data = watsonResponse.getBody().getData();
        List<String> keywords = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            JsonNode keyword = data.get(i);
            keywords.add(keyword.get("text").asText());
            //keywords.add(data.get("text").asText());
        }
        return keywords;
    }

    @Override
    public FrontObj createWatsonResponse(JsonNode watson, String convId) {
        FrontObj response = new FrontObj();
        response.setCon_id(convId);
        response.setText(watson.get("watsonData").asText().split("&")[0]);
        return response;
    }

    @Override
    public FrontObj createAmazonResponse(ResponseEntity<ResponseObject> amazonResponse, String convId) {
        FrontObj response = new FrontObj();
        response.setCon_id(convId);
        response.setText(amazonResponse.getBody().getData().asText());
        return response;
    }

    @Override
    public MongoDbObject getLastObjectFromMongo(String convId) {
        List<MongoDbObject> list =  this.mongoObjRepo.findAllByConvId(convId);
        Collections.sort(list);
        return list.get(0);
    }

    @Override
    public MongoDbObject prepareMongoObjInWelcome(boolean isNew, String convId, SystemResponse contextData) {

        MongoDbObject mongoDbObject = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if(isNew){
            mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),1,0,0, 1, contextData);
        }
        else{
            MongoDbObject first = this.getLastObjectFromMongo(convId);

            if(first.getKeywords().size() > 0){
                HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(first.getKeywords(), headers);
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon/quantity", HttpMethod.POST, entityAmazon, ResponseObject.class);

                mongoDbObject = new MongoDbObject(convId,first.getKeywords(),first.getQuestions()+1,amazonResponse.getBody().getData().asInt(),first.getMisunderstoodQuestions(), first.getCounter()+1, contextData);
            }
            else{
                mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),1,first.getTotalResults(),first.getMisunderstoodQuestions(), first.getCounter()+1, contextData);
            }

        }
        return mongoDbObject;
    }

    @Override
    public MongoDbObject prepareMongoObjInDialog(boolean isNew, List<String> keywords, String convId, SystemResponse context) {
        MongoDbObject mongoDbObject = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(isNew){
            HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(keywords, headers);
            ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon/quantity", HttpMethod.POST, entityAmazon, ResponseObject.class);

            mongoDbObject = new MongoDbObject(convId,keywords,1,amazonResponse.getBody().getData().asInt(),0, 1, context);
        }
        else{
            MongoDbObject first = this.getLastObjectFromMongo(convId);

            List<String> resultKeywords = first.getKeywords();
            keywords.forEach(item -> resultKeywords.add(item));

            if(resultKeywords.size() > 0){
                HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(resultKeywords, headers);
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon/quantity", HttpMethod.POST, entityAmazon, ResponseObject.class);

                mongoDbObject = new MongoDbObject(convId, resultKeywords,first.getQuestions()+1,amazonResponse.getBody().getData().asInt(), first.getMisunderstoodQuestions(), first.getCounter()+1, context);
            }
            else{
                mongoDbObject = new MongoDbObject(convId, new ArrayList<String>(),first.getQuestions()+1,first.getTotalResults(), first.getMisunderstoodQuestions(), first.getCounter()+1, context);
            }
        }
        return mongoDbObject;
    }

    @Override
    public MongoDbObject prepareMongoObjForMisunderstanding(boolean isNew, String convId, SystemResponse context) {
        MongoDbObject mongoDbObject = null;
        if(isNew){
            mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),0,0,1, 1, context);
        }
        else{
            MongoDbObject first = this.getLastObjectFromMongo(convId);
            mongoDbObject = new MongoDbObject(convId,first.getKeywords(),first.getQuestions(),first.getTotalResults(), first.getMisunderstoodQuestions()+1,first.getCounter()+1, context);

        }
        return  mongoDbObject;
    }
}
