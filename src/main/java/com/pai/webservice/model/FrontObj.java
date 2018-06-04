package com.pai.webservice.model;

public class FrontObj {

    private String text;
    private String con_id;

    public FrontObj(String text, String con_id) {
        this.text = text;
        this.con_id = con_id;
    }

    public FrontObj() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCon_id() {
        return con_id;
    }

    public void setCon_id(String con_id) {
        this.con_id = con_id;
    }
}
