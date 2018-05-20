package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.assistant.v1.model.*;
import com.pai.webservice.model.AssistantAnswer;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.service.AmazonResponseService;
import com.pai.webservice.service.AmazonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ibm.watson.developer_cloud.assistant.v1.Assistant;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/watsonAst")
public class AssistantController {

    private static ObjectMapper mapper = new ObjectMapper();


    @Autowired
    private AmazonService amazonService;

    @Autowired
    private AmazonResponseService amazonResponseService;

    @PostMapping(value = "")
    public @ResponseBody
    ResponseEntity processWatson(@Valid @RequestBody String inputText) {

        String conversationID = "";

        Integer toAnalyze = 1;
        Integer giveAmazonURL = 0;

        //connect with Watson - Assistant
        Assistant Assistantservice = new Assistant("2018-02-16");
        Assistantservice.setUsernameAndPassword("eb12a14a-da23-4b5c-9f3f-d756e8f02ec2", "GJGAnVtaLuaP");

        String workspaceId = "bb7f5b28-50f0-49ae-a454-df7799de94a3";

        ListLogsOptions Log3options =
                new ListLogsOptions.Builder(workspaceId).
                        build();


        LogCollection Log3response = Assistantservice.listLogs(Log3options).execute();

        conversationID =        "91fab0f0-7307-4e9a-85cd-8d0d7c497178";
     //   String filter = "response.context.conversation_id::" + conversationID;
        String filter = "logs.response.intents.intent:hello";

        ListAllLogsOptions LogsOptions = new ListAllLogsOptions.Builder(filter).build();

 //       language::en
        LogCollection response = Assistantservice.listAllLogs(LogsOptions).execute();

        //   if (converstaionID == "") {
//LOG 1
            ListLogsOptions Log1options = new ListLogsOptions.Builder(workspaceId).build();

            LogCollection Log1response = Assistantservice.listLogs(Log1options).execute();

            InputData input = new InputData.Builder(inputText).build();

            MessageOptions options = new MessageOptions.Builder(workspaceId)
                    .input(input)
                    .build();

            MessageResponse AssistantResponse = Assistantservice.message(options).execute();

            List<String> data = AssistantResponse.getOutput().getNodesVisited();


        /*       if (data.get(0) == "Welcome" )
               {
                   toAnalyze=0;
               }
        */
            Context test = AssistantResponse.getContext();
//LOG 2
            ListLogsOptions Log2options = new ListLogsOptions.Builder(workspaceId).build();

            LogCollection Log2response = Assistantservice.listLogs(Log2options).execute();

            AssistantAnswer result = new AssistantAnswer(toAnalyze, giveAmazonURL, AssistantResponse.getOutput().getText().get(0).replace("[", "").replace("]", ""));

            JsonNode returnData = mapper.valueToTree(result);
      //  }

     //   else {

/*
            MessageOptions secondMessageOptions = new MessageOptions.Builder()
                    .workspaceId(workspaceId)
                    .input(new InputData.Builder("about history").build())
                    .context(AssistantResponse.getContext()) // output context from the first message
                    .build();

            MessageResponse secondResponse = Assistantservice.message(secondMessageOptions).execute();
 */
    //    }

        return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,returnData), HttpStatus.OK);
    }

}
