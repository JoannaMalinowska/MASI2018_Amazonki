package com.pai.webservice.model;

import lombok.Data;

import java.util.List;

@Data
public class AssistantAnswer {
    Integer toAnalyze;
    Integer getAmazonURL;
    String watsonData;

public AssistantAnswer(){

}

    public AssistantAnswer(Integer toAnalyze, Integer getAmazonURL , String watsonData) {
        this.toAnalyze = toAnalyze;
        this.getAmazonURL = getAmazonURL;
        this.watsonData = watsonData;
    }


}
