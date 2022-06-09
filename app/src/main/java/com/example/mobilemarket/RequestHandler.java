package com.example.mobilemarket;

import org.json.JSONException;

public interface RequestHandler {
    public abstract void processResponse(String response) throws JSONException;
}
