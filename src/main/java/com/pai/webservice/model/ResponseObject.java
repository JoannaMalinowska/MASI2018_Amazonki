package com.pai.webservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * Created by krystian on 19.12.17.
 */
@Data
public class ResponseObject {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    String status;
    String notification;
    JsonNode data;

    public ResponseObject() {

    }

    public ResponseObject(String status, String notification) {
        this.status = status;
        this.notification = notification;
    }

    public ResponseObject(String status, String notification, JsonNode data) {
        this.status = status;
        this.notification = notification;
        this.data = data;
    }

    private static ResponseObject create(String _status, String _notification, JsonNode... _data) {
        ResponseObject r = new ResponseObject();
        r.setStatus(_status);
        r.setNotification(_notification);

        if (_data.length > 0) {
            r.setData(_data[0]);
        }

        return r;
    }

    public static ResponseObject createSuccess(String _notification, JsonNode... _data) {
        return create(SUCCESS, _notification, _data);
    }

    public static ResponseObject createError(String _notification, JsonNode... _data) {
        return create(ERROR, _notification, _data);
    }


}
