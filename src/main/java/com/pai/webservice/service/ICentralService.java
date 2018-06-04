package com.pai.webservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.ResponseObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICentralService {
    public int countWords(String s);
    List<String> getKeywordsFromWatson(FrontObj input);
    FrontObj createWatsonResponse(JsonNode watson, String convId);
    FrontObj createAmazonResponse(ResponseEntity<ResponseObject> response, String convId);
    MongoDbObject getLastObjectFromMongo(String convId);
    MongoDbObject prepareMongoObjInWelcome(boolean isNew, String convId);
    MongoDbObject prepareMongoObjInDialog(boolean isNew, List<String> keywords, String convId);
    MongoDbObject prepareMongoObjForMisunderstanding(boolean isNew, String convId);
}
