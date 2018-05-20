package com.pai.webservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class MongoDbObject {

    public MongoDbObject(String convId, List<String> keywords, int questions) {
        this.convId = convId;
        this.keywords = keywords;
        this.questions = questions;
    }
    @Id
    private String id;
    private  String convId;
    private List<String> keywords;
    private int questions;

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
