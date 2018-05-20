package com.pai.webservice.model;

import lombok.Data;

import java.util.List;

@Data
public class AssistantAnswer {
    String watsonData;

public AssistantAnswer(){

}

    public AssistantAnswer( String watsonData) {
        this.watsonData = watsonData;
    }


}
