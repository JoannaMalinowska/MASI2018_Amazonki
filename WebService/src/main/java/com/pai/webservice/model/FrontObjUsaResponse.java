package com.pai.webservice.model;

public class FrontObjUsaResponse {
    private String convId;
    private String usability;
    private String effectiveness;

    public FrontObjUsaResponse(String effectiveness, String usability) {
        this.usability = usability;
        this.effectiveness = effectiveness;
    }

    public FrontObjUsaResponse(String convId, String effectiveness, String usability) {
        this.convId = convId;
        this.usability = usability;
        this.effectiveness = effectiveness;
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
