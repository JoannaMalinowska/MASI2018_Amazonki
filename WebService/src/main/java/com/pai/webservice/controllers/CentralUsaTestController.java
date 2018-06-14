package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.FrontObjUsaResponse;
import com.pai.webservice.model.MongoDbObjectUsa;
import com.pai.webservice.model.ResponseObject;
import com.pai.webservice.repository.IMongoObjRepoUsa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class CentralUsaTestController {


    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IMongoObjRepoUsa mongoObjRepoUsa;

    @RequestMapping(value = "/centralTest", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<FrontObjUsaResponse> processDialog(@PathVariable String input) {
    /*
        //zapis:
        Date date = new Date();

        int usa = (int) Math.random() * 10;
        String usa2 = Integer.toString(usa);
        int eff = (int) Math.random() * 10;
        String eff2 = Integer.toString(eff);

        MongoDbObjectUsa mongoDbObject = null;
        mongoDbObject = new MongoDbObjectUsa("USA"+ date.getTime(), usa2, eff2);
        System.out.println("USA"+ date.getTime());
        mongoObjRepoUsa.save(mongoDbObject);

        ResponseEntity<ResponseObject> amazonResponse = restTemplate.exchange("http://localhost:8080/api/amazon/quantity", HttpMethod.POST, entityAmazon, ResponseObject.class);

        //odczyt

         List<MongoDbObjectUsa> list = this.mongoObjRepoUsa.findAllByConvId("USA");
        for (MongoDbObjectUsa md : list){
            System.out.println(md.getConvId() + md.getEfectiveness() + md.getUsability());
        }

*/

        return new ResponseEntity<FrontObjUsaResponse>(new FrontObjUsaResponse("", "", ""), HttpStatus.OK);
    }



}
