package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.assistant.v1.model.*;
import com.pai.webservice.model.*;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.repository.IMongoObjRepo;
import com.pai.webservice.service.AmazonResponseService;
import com.pai.webservice.service.AmazonService;
import com.pai.webservice.service.CentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import com.ibm.watson.developer_cloud.assistant.v1.Assistant;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/watsonAst")
public class AssistantController {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IMongoObjRepo mongoObjRepo;

    @Autowired
    private Environment environment;

    @PostMapping(value = "")
    @Async
    public @ResponseBody
    ResponseEntity<ResponseObject> processWatson(@Valid @RequestBody FrontObj inputFront) {

        String conversationID = inputFront.getCon_id();

        Assistant Assistantservice = new Assistant("2018-02-16");
        Assistantservice.setUsernameAndPassword(environment.getProperty("assistantWatson.username"), environment.getProperty("assistantWatson.password"));

        AssistantAnswer result=null;
        JsonNode returnData = null;

           if (conversationID.equals("-1")) {

            InputData input = new InputData.Builder(inputFront.getText()).build();

            MessageOptions options = new MessageOptions.Builder(environment.getProperty("assistantWatson.workspace"))
                    .context(new Context()).input(input)
                    .build();

            MessageResponse AssistantResponse = Assistantservice.message(options).execute();

            result = new AssistantAnswer(AssistantResponse.getOutput().getText().get(0).replace("[", "").replace("]", ""));

            WatsonConv respone  = new WatsonConv();
            respone.setCon_id(AssistantResponse.getContext().getConversationId());
            respone.setAssistantAnswer(result);

            respone.setSystemResponse(AssistantResponse.getContext().getSystem());

            returnData = mapper.valueToTree(respone);
        }

        else {

           List<MongoDbObject> list = this.mongoObjRepo.findAllByConvId(conversationID);
           Collections.sort(list);
           MongoDbObject first = list.get(0);

            MessageOptions secondMessageOptions = new MessageOptions.Builder()
                    .workspaceId(environment.getProperty("assistantWatson.workspace"))
                    .input(new InputData.Builder(inputFront.getText()).build())
                    .context(new Context())
                    .build();

           secondMessageOptions.context().setConversationId(conversationID);
           secondMessageOptions.context().setSystem(first.getContext());
           MessageResponse secondResponse = Assistantservice.message(secondMessageOptions).execute();

           WatsonConv respone  = new WatsonConv();
           try {
               result = new AssistantAnswer(secondResponse.getOutput().getText().get(0).replace("[", "").replace("]", ""));

               respone.setCon_id(secondResponse.getContext().getConversationId());
               respone.setSystemResponse(secondResponse.getContext().getSystem());

           }

           catch(Exception e){
               result = new AssistantAnswer("I didn't understand.&G");
               respone.setCon_id(conversationID);
               respone.setSystemResponse(secondResponse.getContext().getSystem());
           }
           respone.setAssistantAnswer(result);

           returnData = mapper.valueToTree(respone);

        }

        return new ResponseEntity<ResponseObject>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS,returnData), HttpStatus.OK);
    }

}
