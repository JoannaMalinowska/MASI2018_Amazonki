package com.pai.webservice.model;

import com.ibm.watson.developer_cloud.assistant.v1.model.SystemResponse;

public class WatsonConv {

    private String con_id;
    private AssistantAnswer assistantAnswer;
    private SystemResponse systemResponse;

    public String getCon_id() {
        return con_id;
    }

    public void setCon_id(String con_id) {
        this.con_id = con_id;
    }

    public AssistantAnswer getAssistantAnswer() {
        return assistantAnswer;
    }

    public void setAssistantAnswer(AssistantAnswer assistantAnswer) {
        this.assistantAnswer = assistantAnswer;
    }

    public SystemResponse getSystemResponse() {
        return systemResponse;
    }

    public void setSystemResponse(SystemResponse systemResponse) {
        this.systemResponse = systemResponse;
    }
}
