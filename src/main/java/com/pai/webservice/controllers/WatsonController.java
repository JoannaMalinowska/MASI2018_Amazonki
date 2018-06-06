package com.pai.webservice.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.service.exception.ServiceResponseException;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.service.AmazonResponseService;
import com.pai.webservice.service.AmazonService;
import com.pai.webservice.service.CentralService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import com.ibm.watson.developer_cloud.assistant.v1.Assistant;
import com.ibm.watson.developer_cloud.assistant.v1.model.CreateWorkspaceOptions;
import com.ibm.watson.developer_cloud.assistant.v1.model.InputData;
import com.ibm.watson.developer_cloud.assistant.v1.model.IntentCollection;
import com.ibm.watson.developer_cloud.assistant.v1.model.ListIntentsOptions;
import com.ibm.watson.developer_cloud.assistant.v1.model.ListWorkspacesOptions;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v1.model.Workspace;
import com.ibm.watson.developer_cloud.assistant.v1.model.WorkspaceCollection;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.*;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/watson")
public class WatsonController {

    private static ObjectMapper mapper = new ObjectMapper();


    @Autowired
    private AmazonService amazonService;

    @Autowired
    private AmazonResponseService amazonResponseService;


    @Autowired
    private CentralService centralService;

    @PostMapping(value = "")
    @Async
    public @ResponseBody ResponseEntity<ResponseObject> processWatson(@Valid @RequestBody String input) {

        NaturalLanguageUnderstanding NLUservice = new NaturalLanguageUnderstanding(
                "2018-03-16",
                "0f2064df-e2a9-4605-a74b-f3c7607a22e9",
                //"219783e0-f7c9-47f4-9c30-4baf3eaa424c",
                "yWrIIsBmlKXQ"
                //"t8TsyDcM6C8W"
        );
        KeywordsOptions keywordsOptions = new KeywordsOptions.Builder()
                .build();

        Features features = new Features.Builder()
                .keywords(keywordsOptions)
                .build();


        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(input)
                .features(features)
                .build();

        AnalysisResults nluResponse = null;
        JsonNode returnData = null;
        try {
            System.out.println(parameters.toString() + ", , " + parameters.text() + ", " + parameters.features());


            nluResponse = NLUservice
                    .analyze(parameters)
                    .execute();
            returnData = mapper.valueToTree(  nluResponse.getKeywords());
        } catch (ServiceResponseException ex) {
            JSONObject obj = new JSONObject();
            obj.put("text", input);
            returnData = mapper.valueToTree(new ArrayList<String>(){{add(obj.toJSONString());}});
        }

        return new ResponseEntity<ResponseObject>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,returnData), HttpStatus.OK);

    }

}
