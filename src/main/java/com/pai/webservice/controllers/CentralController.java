package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.FrontObj;
import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.notifications.Notification;
import com.pai.webservice.repository.IMongoObjRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/central")
public class CentralController {


    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IMongoObjRepo mongoObjRepo;

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
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon", HttpMethod.POST, entityAmazon, ResponseObject.class);
                JsonNode returnData = mapper.valueToTree(amazonResponse.getBody().getData());

                return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
            }
        }


        HttpEntity<FrontObj> entityWatsonAss = new HttpEntity<FrontObj>(input, headers);
        System.out.println(entityWatsonAss);

        ResponseEntity<ResponseObject> watsonAssResponse;
        JsonNode assData;
        String convId;
        try {
            watsonAssResponse = restTemplate.exchange("http://localhost:8080/api/watsonAst", HttpMethod.POST,entityWatsonAss,ResponseObject.class);
            //System.out.println(watsonAssResponse);

            ResponseObject aaa =  watsonAssResponse.getBody();
            //assData = watsonAssResponse.getBody().getData().get("assistantAnswer");
            convId = aaa.getData().get("con_id").asText();
            assData = aaa.getData().get("assistantAnswer");

            System.out.println(watsonAssResponse);
            System.out.println(watsonAssResponse.getBody().getData());

             //convId = watsonAssResponse.getBody().getData().get("con_id").asText();

        } catch (Exception ex){ //Powinno byc RestClientException
            //jesli nie dostaniemy potrzebnych danych to obsluga dalsza nie ma sensu
            System.out.println("Excetion " + ex.toString());
            ex.printStackTrace();
            return new ResponseEntity<ResponseObject>(new ResponseObject("status", "notification"), HttpStatus.OK);
        }
       // if(watsonAssResponse.getBody() == null) {
         //   return new ResponseEntity<ResponseObject>(new ResponseObject("status", "notification"), HttpStatus.OK);
        //}



        System.out.println(assData);

        //begin conversation
        //Do przepisania na if - else if - else if - if
        if (assData.size() > 0 && assData.get("watsonData") !=null && assData.get("watsonData").asText().contains("&") && assData.get("watsonData").asText().split("&")[1].equals("W")) {


            FrontObj response = new FrontObj();
            response.setCon_id(convId);
            response.setText(assData.get("watsonData").asText().split("&")[0]);

            JsonNode returnData = mapper.valueToTree(response);

            MongoDbObject mongoDbObject = null;

            if(isNew){
                mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),1,0,0, 1);
            }
            else{
                List<MongoDbObject> list =  this.mongoObjRepo.findAllByConvId(convId);
                Collections.sort(list);
                MongoDbObject first = list.get(0);

                if(first.getKeywords().size() > 0){
                    HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(first.getKeywords(), headers);
                    ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon/quantity", HttpMethod.POST, entityAmazon, ResponseObject.class);

                    mongoDbObject = new MongoDbObject(convId,first.getKeywords(),first.getQuestions()+1,amazonResponse.getBody().getData().asInt(),first.getMisunderstoodQuestions(), first.getCounter()+1);
                }
                else{
                    mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),1,first.getTotalResults(),first.getMisunderstoodQuestions(), first.getCounter()+1);
                }

            }
            mongoObjRepo.save(mongoDbObject);

            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);

        }

        //dialog
        if (assData.size() > 0 && assData.get("watsonData") !=null && assData.get("watsonData").asText().contains("&") && assData.get("watsonData").asText().split("&")[1].equals("T")) {
            HttpEntity<String> entityWatson = new HttpEntity<String>(input.getText(), headers);
            ResponseEntity<ResponseObject> watsonResponse = restTemplate.exchange("http://localhost:8080/api/watson", HttpMethod.POST, entityWatson, ResponseObject.class);

            JsonNode data = watsonResponse.getBody().getData();
            List<String> keywords = new ArrayList<>();

            for (int i = 0; i < data.size(); i++) {
                JsonNode keyword = data.get(i);
                keywords.add(keyword.get("text").asText());
            }

            MongoDbObject mongoDbObject = null;

            if(isNew){
                HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(keywords, headers);
                ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon/quantity", HttpMethod.POST, entityAmazon, ResponseObject.class);

                mongoDbObject = new MongoDbObject(convId,keywords,1,amazonResponse.getBody().getData().asInt(),0, 1);
            }
            else{
                List<MongoDbObject> list =  this.mongoObjRepo.findAllByConvId(convId);
                Collections.sort(list);
                MongoDbObject first = list.get(0);

                List<String> resultKeywords = first.getKeywords();
                keywords.forEach(item -> resultKeywords.add(item));

                if(resultKeywords.size() > 0){
                    HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(resultKeywords, headers);
                    ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon/quantity", HttpMethod.POST, entityAmazon, ResponseObject.class);

                    mongoDbObject = new MongoDbObject(convId, resultKeywords,first.getQuestions()+1,amazonResponse.getBody().getData().asInt(), first.getMisunderstoodQuestions(), first.getCounter()+1);
                }
                else{
                    mongoDbObject = new MongoDbObject(convId, new ArrayList<String>(),first.getQuestions()+1,first.getTotalResults(), first.getMisunderstoodQuestions(), first.getCounter()+1);
                }


            }
            mongoObjRepo.save(mongoDbObject);

            FrontObj response = new FrontObj();
            response.setCon_id(convId);
            response.setText(assData.get("watsonData").asText().split("&")[0]);

            JsonNode returnData = mapper.valueToTree(response);

            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);

        }


        //end conversation
        if(assData.size() > 0 && assData.get("watsonData") !=null && assData.get("watsonData").asText().contains("&") && assData.get("watsonData").asText().split("&")[1].equals("K")){

            MongoDbObject mongoDbObject = null;
            List<String> resultKeywords = null;
            if(isNew){
                JsonNode returnData = mapper.valueToTree("Zle");

                return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
            }
            else{
                List<MongoDbObject> list =  this.mongoObjRepo.findAllByConvId(convId);
                Collections.sort(list);
                MongoDbObject first =list.get(0);

                resultKeywords = first.getKeywords();
                if(resultKeywords.size()< 1 ){
                    JsonNode returnData = mapper.valueToTree("Zle");

                    return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
                }
            }

            HttpEntity<List<String>> entityAmazon = new HttpEntity<List<String>>(resultKeywords, headers);
            ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon", HttpMethod.POST, entityAmazon, ResponseObject.class);

            FrontObj response = new FrontObj();
            response.setCon_id(convId);
            response.setText(amazonResponse.getBody().getData().asText());

            JsonNode returnData = mapper.valueToTree(response);

            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
        }

        else {
            FrontObj response = new FrontObj();
            response.setCon_id(convId);
            response.setText(assData.get("watsonData").asText().split("&")[0]);

            JsonNode returnData = mapper.valueToTree(response);

            MongoDbObject mongoDbObject = null;

            if(isNew){
                mongoDbObject = new MongoDbObject(convId,new ArrayList<String>(),0,0,1, 1);
            }
            else{
                List<MongoDbObject> list =  this.mongoObjRepo.findAllByConvId(convId);
                Collections.sort(list);
                MongoDbObject first = list.get(0);
                mongoDbObject = new MongoDbObject(convId,first.getKeywords(),first.getQuestions(),first.getTotalResults(), first.getMisunderstoodQuestions()+1,first.getCounter()+1);

            }

            mongoObjRepo.save(mongoDbObject);
            return new ResponseEntity<>(ResponseObject.createSuccess(Notification.TEST_GET_SUCCESS, returnData), HttpStatus.OK);
        }


    }

}
