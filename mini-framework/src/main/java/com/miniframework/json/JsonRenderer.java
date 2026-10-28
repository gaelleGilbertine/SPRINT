package com.miniframework.json;

import com.google.gson.Gson;

/** Sérialise un objet Java en JSON (via Gson). */
public class JsonRenderer {

    private final Gson gson = new Gson();

    public String toJson(Object data) {
        return gson.toJson(data);
    }
}
