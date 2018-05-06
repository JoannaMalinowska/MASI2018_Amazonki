package com.pai.webservice.controllers;

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

@RestController
@RequestMapping(value = "/watson")
public class WatsonController {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AmazonService amazonService;

    @Autowired
    private AmazonResponseService amazonResponseService;

    @PostMapping(value = "")
    public @ResponseBody
    ResponseEntity processWatson(@Valid @RequestBody String inputText) {



        //connect with Watson - Assistant
        Assistant Assistantservice = new Assistant("2018-02-16");
        Assistantservice.setUsernameAndPassword("eb12a14a-da23-4b5c-9f3f-d756e8f02ec2", "GJGAnVtaLuaP");

        String workspaceId = "bb7f5b28-50f0-49ae-a454-df7799de94a3";

//Input string
        //String InputText ="Give me a funny book and tasty toast";

//Send InputText to analyze by Watson Assistant
        InputData input = new InputData.Builder(inputText).build();

        MessageOptions options = new MessageOptions.Builder(workspaceId)
                .input(input)
                .build();

        MessageResponse AssistantResponse = Assistantservice.message(options).execute();

        //     System.out.println(AssistantResponse.getOutput().getText());
        //      System.out.println(AssistantResponse.getIntents());
        //      System.out.println(AssistantResponse.getEntities());

//connect with Watson - Natural Language Understanding
        NaturalLanguageUnderstanding NLUservice = new NaturalLanguageUnderstanding(
                "2018-03-16",
                "219783e0-f7c9-47f4-9c30-4baf3eaa424c",
                "t8TsyDcM6C8W"
        );

//Set analyze options
        KeywordsOptions keywordsOptions = new KeywordsOptions.Builder()
                .build();

        Features features = new Features.Builder()
                .keywords(keywordsOptions)
                .build();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(inputText)
                .features(features)
                .build();

//Analyze inputText in Watson - NLU
        AnalysisResults NLUresponse = NLUservice
                .analyze(parameters)
                .execute();



        JsonNode returnData = mapper.valueToTree(  NLUresponse.getKeywords());
        return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,returnData), HttpStatus.OK);
//return new ResponseEntity<> (ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, NLUresponse.getKeywords()),HttpStatus.OK);
        //   System.out.println(NLUresponse.getKeywords());



    /*    amazonService.setSearchCategory("Books");
        amazonService.prepareKeywordsForRequest(new ArrayList<String>(){{add("Java");}});

        amazonResponseService.setAmazonResponse(amazonService.getResultFromRequest());

        JsonNode returnData = mapper.valueToTree(amazonResponseService.getQuantityResults());
        return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, ), HttpStatus.OK);
        */
    }

}
