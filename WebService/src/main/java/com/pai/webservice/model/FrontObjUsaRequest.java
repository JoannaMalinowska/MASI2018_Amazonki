package com.pai.webservice.model;

public class FrontObjUsaRequest {
    private String usability;
    private String effectiveness;

    public FrontObjUsaRequest(){
    }

    public FrontObjUsaRequest(String effectiveness, String usability) {
        this.usability = usability;
        this.effectiveness = effectiveness;
    }

    public String getUsability() {
        return usability;
    }

    public String getEffectiveness() {
        return effectiveness;
    }

    public void setUsability(String usability) {
        this.usability = usability;
    }

    public void setEffectiveness(String effectiveness) {
        this.effectiveness = effectiveness;
    }
}
