package com.pai.webservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.watson.developer_cloud.assistant.v1.model.SystemResponse;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.ResponseObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICentralService {
    boolean checkEndConversation(String convId);
    List<String> getKeywordsFromWatson(FrontObj input);
    FrontObj createWatsonResponse(JsonNode watson, String convId);
    FrontObj createAmazonResponse(ResponseEntity<ResponseObject> response, String convId);
    MongoDbObject getLastObjectFromMongo(String convId);
    MongoDbObject prepareMongoObjInWelcome(boolean isNew, String convId, SystemResponse context);
    MongoDbObject prepareMongoObjInDialog(boolean isNew, List<String> keywords, String convId, SystemResponse context);
    MongoDbObject prepareMongoObjForMisunderstanding(boolean isNew, String convId, SystemResponse context);
    MongoDbObject prepareMongoObjForEnding(boolean isNew, List<String> keywords, String convId, SystemResponse context);
}
