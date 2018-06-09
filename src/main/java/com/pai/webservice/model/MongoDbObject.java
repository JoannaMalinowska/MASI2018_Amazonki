package com.pai.webservice.model;

import com.ibm.watson.developer_cloud.assistant.v1.model.SystemResponse;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Data
public class MongoDbObject implements Comparable<MongoDbObject> {

    public MongoDbObject(String convId, List<String> keywords, int questions, Integer totalResults, Integer misunderstoodQuestions, int counter, SystemResponse context) {
        this.convId = convId;
        this.keywords = keywords;
        this.questions = questions;
        this.totalResults = totalResults;
        this.misunderstoodQuestions = misunderstoodQuestions;
        this.context = context;
        this.counter = counter;
    }
    @Id
    private String id;
    private  String convId;
    private List<String> keywords;
    private int questions;
    private Integer totalResults;
    private  Integer misunderstoodQuestions;
    private SystemResponse context;
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

    public SystemResponse getContext() {
        return context;
    }

    public void setContext(SystemResponse context) {
        this.context = context;
    }

    @Override
    public int compareTo(MongoDbObject o) {
        return o.counter - this.counter;
    }


}
