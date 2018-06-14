package com.pai.webservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * Created by krystian on 19.12.17.
 */
@Data
public class ResponseObjectUsa {
   // public static final String SUCCESS = "success";
   // public static final String ERROR = "error";

    String usability;
    String notification;

    public ResponseObjectUsa() {
        super();
    }

    public ResponseObjectUsa(String usability, String notification) {
        this.usability = usability;
        this.notification = notification;
    }

    public String getUsability() {
        return usability;
    }

    public void setUsability(String usability) {
        this.usability = usability;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    /*
    private static ResponseObjectUsa create(String _status, String _notification, JsonNode... _data) {
        ResponseObjectUsa r = new ResponseObjectUsa();
        r.setStatus(_status);
        r.setNotification(_notification);

        if (_data.length > 0) {
            r.setData(_data[0]);
        }

        return r;
    }

    public static ResponseObjectUsa createSuccess(String _notification, JsonNode... _data) {
        return create(SUCCESS, _notification, _data);
    }

    public static ResponseObjectUsa createError(String _notification, JsonNode... _data) {
        return create(ERROR, _notification, _data);
    }

    public JsonNode getData(){
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getNotification() {
        return notification;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    */
}
