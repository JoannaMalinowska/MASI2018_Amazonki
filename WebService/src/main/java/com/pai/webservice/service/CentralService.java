package com.pai.webservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.assistant.v1.model.SystemResponse;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.model.WatsonAssistantObject;
import com.pai.webservice.repository.IMongoObjRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    @Autowired
    private Environment environment;

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean checkEndConversation(String convId) {
        MongoDbObject first = this.getLastObjectFromMongo(convId);
        if(first.getKeywords().size() < 1){
            return false;
        }
        return false;
    }

    @Override
    public List<String> getKeywordsFromWatson(FrontObj input) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entityWatson = new HttpEntity<String>(input.getText(), headers);
        ResponseEntity<ResponseObject> watsonResponse = restTemplate.exchange(environment.getProperty("watsonURL.path"), HttpMethod.POST, entityWatson, ResponseObject.class);

        JsonNode data = watsonResponse.getBody().getData();
        List<String> keywords = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            JsonNode keyword = data.get(i);
            keywords.add(keyword.get("text").asText());
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
            mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),1,0,0, 1, contextData,0);
        }
        else{
            MongoDbObject first = this.getLastObjectFromMongo(convId);

            if(first.getKeywords().size() > 0){
                HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(first.getKeywords(), headers);
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange(environment.getProperty("amazonQuantityResultURL.path"), HttpMethod.POST, entityAmazon, ResponseObject.class);

                double reduction = (double)((first.getTotalResults()-amazonResponse.getBody().getData().asInt())/(first.getTotalResults()*1.0))*100;
                reduction = Math.round(reduction * 100.0) / 100.0;
                mongoDbObject = new MongoDbObject(convId,first.getKeywords(),first.getQuestions()+1,amazonResponse.getBody().getData().asInt(),first.getMisunderstoodQuestions(), first.getCounter()+1, contextData,reduction);
            }
            else{

                mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),1,first.getTotalResults(),first.getMisunderstoodQuestions(), first.getCounter()+1, contextData,0);
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
            ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange(environment.getProperty("amazonQuantityResultURL.path"), HttpMethod.POST, entityAmazon, ResponseObject.class);


            mongoDbObject = new MongoDbObject(convId,keywords,1,amazonResponse.getBody().getData().asInt(),0, 1, context,0);
        }
        else{
            MongoDbObject first = this.getLastObjectFromMongo(convId);

            List<String> resultKeywords = first.getKeywords();
            keywords.forEach(item -> resultKeywords.add(item));

            if(resultKeywords.size() > 0){
                HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(resultKeywords, headers);
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange(environment.getProperty("amazonQuantityResultURL.path"), HttpMethod.POST, entityAmazon, ResponseObject.class);

                double reduction = (double)((first.getTotalResults()-amazonResponse.getBody().getData().asInt())/(first.getTotalResults()*1.0))*100;
                reduction = Math.round(reduction * 100.0) / 100.0;

                mongoDbObject = new MongoDbObject(convId, resultKeywords,first.getQuestions()+1,amazonResponse.getBody().getData().asInt(), first.getMisunderstoodQuestions(), first.getCounter()+1, context,reduction);
            }
            else{
                mongoDbObject = new MongoDbObject(convId, new ArrayList<String>(),first.getQuestions()+1,first.getTotalResults(), first.getMisunderstoodQuestions(), first.getCounter()+1, context, 0);
            }
        }
        return mongoDbObject;
    }

    @Override
    public MongoDbObject prepareMongoObjForEnding(boolean isNew, List<String> keywords, String convId, SystemResponse context) {
        MongoDbObject mongoDbObject = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if(isNew){
            mongoDbObject = new MongoDbObject(convId, new ArrayList<>(), 0, 0, 0, 1, context, 0);
        }
        else {
            MongoDbObject first = this.getLastObjectFromMongo(convId);
            List<String> resultKeywords = first.getKeywords();
            keywords.forEach(item -> resultKeywords.add(item));

            HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(resultKeywords, headers);
            ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange(environment.getProperty("amazonQuantityResultURL.path"), HttpMethod.POST, entityAmazon, ResponseObject.class);

            double reduction = (double)((first.getTotalResults()-amazonResponse.getBody().getData().asInt())/(first.getTotalResults()*1.0))*100;
            reduction = Math.round(reduction * 100.0) / 100.0;

            mongoDbObject = new MongoDbObject(convId, resultKeywords,first.getQuestions()+1,amazonResponse.getBody().getData().asInt(), first.getMisunderstoodQuestions(), first.getCounter()+1, context,reduction);
        }
        return mongoDbObject;
    }

    @Override
    public WatsonAssistantObject getWatsonAssistantResponse(FrontObj input) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FrontObj> entityWatsonAss = new HttpEntity<FrontObj>(input, headers);
        ResponseEntity<ResponseObject> watsonAssistantResponse;watsonAssistantResponse = restTemplate.exchange(environment.getProperty("watsonAssistantURL.path"),
                HttpMethod.POST,entityWatsonAss,ResponseObject.class);

        ResponseObject watsonResponse =  watsonAssistantResponse.getBody();
        WatsonAssistantObject watsonAssistantObject = new WatsonAssistantObject(watsonResponse.getData().get("assistantAnswer"),
                watsonResponse.getData().get("systemResponse"), watsonResponse.getData().get("con_id").asText(), mapper.treeToValue(watsonResponse.getData().get("systemResponse"), SystemResponse.class));
        return watsonAssistantObject;
    }

    @Override
    public MongoDbObject prepareMongoObjForMisunderstanding(boolean isNew, String convId, SystemResponse context) {
        MongoDbObject mongoDbObject = null;
        if(isNew){
            mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),0,0,1, 1, context, 0);
        }
        else{
            MongoDbObject first = this.getLastObjectFromMongo(convId);
            mongoDbObject = new MongoDbObject(convId,first.getKeywords(),first.getQuestions(),first.getTotalResults(), first.getMisunderstoodQuestions()+1,first.getCounter()+1, context, 0);

        }
        return  mongoDbObject;
    }
}
