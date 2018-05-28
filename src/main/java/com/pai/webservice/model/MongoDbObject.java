package com.pai.webservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Data
public class MongoDbObject implements Comparable<MongoDbObject> {

    public MongoDbObject(String convId, List<String> keywords, int questions, Integer totalResults, Integer misunderstoodQuestions, int counter) {
        this.convId = convId;
        this.keywords = keywords;
        this.questions = questions;
        this.totalResults = totalResults;
        this.misunderstoodQuestions = misunderstoodQuestions;
        this.counter = counter;
    }
    @Id
    private String id;
    private  String convId;
    private List<String> keywords;
    private int questions;
    private Integer totalResults;
    private  Integer misunderstoodQuestions;
    private int counter;

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

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getMisunderstoodQuestions() {
        return misunderstoodQuestions;
    }

    public void setMisunderstoodQuestions(Integer misunderstoodQuestions) {
        this.misunderstoodQuestions = misunderstoodQuestions;
    }

    public int getCounter() {
        return counter;
    }

    public void setDate(int counter) {
        this.counter = counter;
    }

    @Override
    public int compareTo(MongoDbObject o) {
        return o.counter - this.counter;
    }


}
