package com.pai.webservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pai.webservice.model.FrontObjUsaResponse;
import com.pai.webservice.model.MongoDbObjectUsa;
import com.pai.webservice.repository.IMongoObjRepoUsa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CentralUsaGetController {


    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IMongoObjRepoUsa mongoObjRepoUsa;

    @RequestMapping(value = "/centralUsaGet/{input}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<FrontObjUsaResponse> processDialog(@PathVariable String input) {

        System.out.println(input);

         List<MongoDbObjectUsa> list = this.mongoObjRepoUsa.findAllByConvId(input);
        for (MongoDbObjectUsa md : list){
            System.out.println(md.getConvId() + md.getEfectiveness() + md.getUsability());
        }
        return new ResponseEntity<FrontObjUsaResponse>(new FrontObjUsaResponse(list.get(0).getConvId(), list.get(0).getEfectiveness(), list.get(0).getUsability()), HttpStatus.OK);

    }


}
