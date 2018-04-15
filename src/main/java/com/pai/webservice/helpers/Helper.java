package com.pai.webservice.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Helper {

    private static ObjectMapper mapper = new ObjectMapper();

    public static JsonNode createJson() {
        JsonNode node = mapper.createObjectNode();
        return node;
    }

    public static void addSimpleProperty(JsonNode json, String property, String obj) {
        ((ObjectNode) json).put(property, obj);
    }

    public static void addProperty(JsonNode json, String property, JsonNode obj) {
        ((ObjectNode) json).set(property, obj);
    }
}
