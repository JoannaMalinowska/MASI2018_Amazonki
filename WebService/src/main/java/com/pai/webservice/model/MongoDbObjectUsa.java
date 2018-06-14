package com.pai.webservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class MongoDbObjectUsa implements Comparable<MongoDbObjectUsa> {

    public MongoDbObjectUsa(String convId, String usability, String efectiveness) {
        this.convId=convId;
        this.usability = usability;
        this.efectiveness = efectiveness;
    }
    @Id
    private String id;
    private String convId;
    private  String usability;
    private  String efectiveness;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public String getUsability() {
        return usability;
    }

    public void setUsability(String usability) {
        this.usability = usability;
    }

    public String getEfectiveness() {
        return efectiveness;
    }

    public void setEfectiveness(String efectiveness) {
        this.efectiveness = efectiveness;
    }

    @Override
    public int compareTo(MongoDbObjectUsa o) {//na pozniej do zastanwoienia
        if(o.getConvId().equals(this.convId)){
            return 0;
        } else {
            return 1;
            //lub return -1
        }
    }


}
