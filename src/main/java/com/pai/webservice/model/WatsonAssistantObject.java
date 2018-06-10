package com.pai.webservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.watson.developer_cloud.assistant.v1.model.SystemResponse;

public class WatsonAssistantObject {

    private JsonNode assistantData;
    private JsonNode contextData;
    private String convId;
    private SystemResponse context;


    public WatsonAssistantObject(JsonNode assistantData, JsonNode contextData, String convId, SystemResponse context) {
        this.assistantData = assistantData;
        this.contextData = contextData;
        this.convId = convId;
        this.context = context;
    }

    public JsonNode getAssistantData() {
        return assistantData;
    }

    public void setAssistantData(JsonNode assistantData) {
        this.assistantData = assistantData;
    }

    public JsonNode getContextData() {
        return contextData;
    }

    public void setContextData(JsonNode contextData) {
        this.contextData = contextData;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public SystemResponse getContext() {
        return context;
    }

    public void setContext(SystemResponse context) {
        this.context = context;
    }
}
