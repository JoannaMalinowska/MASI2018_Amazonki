package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.assistant.v1.model.*;
import com.pai.webservice.model.AssistantAnswer;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.model.WatsonConv;
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
    ResponseEntity processWatson(@Valid @RequestBody FrontObj inputFront) {


        String conversationID = inputFront.getCon_id();


        //connect with Watson - Assistant
        Assistant Assistantservice = new Assistant("2018-02-16");
        Assistantservice.setUsernameAndPassword("eb12a14a-da23-4b5c-9f3f-d756e8f02ec2", "GJGAnVtaLuaP");

        String workspaceId = "bb7f5b28-50f0-49ae-a454-df7799de94a3";

        ListLogsOptions Log3options =
                new ListLogsOptions.Builder(workspaceId).
                        build();


        LogCollection Log3response = Assistantservice.listLogs(Log3options).execute();

        JsonNode returnData = null;

           if (conversationID.equals("-1")) {

            InputData input = new InputData.Builder(inputFront.getText()).build();

            MessageOptions options = new MessageOptions.Builder(workspaceId)
                    .input(input)
                    .build();

            MessageResponse AssistantResponse = Assistantservice.message(options).execute();

            List<String> data = AssistantResponse.getOutput().getNodesVisited();

            //ListLogsOptions Log2options = new ListLogsOptions.Builder(workspaceId).build();

            //LogCollection Log2response = Assistantservice.listLogs(Log2options).execute();

            AssistantAnswer result = new AssistantAnswer(AssistantResponse.getOutput().getText().get(0).replace("[", "").replace("]", ""));

            WatsonConv respone  = new WatsonConv();
            respone.setCon_id(AssistantResponse.getContext().getConversationId());
            respone.setAssistantAnswer(result);

            returnData = mapper.valueToTree(respone);
        }

        else {

               ListLogsOptions Log1options = new ListLogsOptions.Builder(workspaceId).build();

               LogCollection Log1response = Assistantservice.listLogs(Log1options).execute();
               Context context = null;

               for(int i=0; i< Log1response.getLogs().size();i++)
               {
                   LogExport logExport = Log1response.getLogs().get(i);
                   context =  logExport.getResponse().getContext();
                   String watsonConversationId =  context.getConversationId();
                   if(watsonConversationId.equals(conversationID))
                   {
                       break;
                   }
               }

            MessageOptions secondMessageOptions = new MessageOptions.Builder()
                    .workspaceId(workspaceId)
                    .input(new InputData.Builder(inputFront.getText()).build())
                    .context(context) // output context from the first message
                    .build();

            MessageResponse secondResponse = Assistantservice.message(secondMessageOptions).execute();

            AssistantAnswer result = new AssistantAnswer(secondResponse.getOutput().getText().get(0).replace("[", "").replace("]", ""));

               WatsonConv respone  = new WatsonConv();
               respone.setCon_id(secondResponse.getContext().getConversationId());
               respone.setAssistantAnswer(result);

            returnData = mapper.valueToTree(respone);

        }

        return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,returnData), HttpStatus.OK);
    }

}
