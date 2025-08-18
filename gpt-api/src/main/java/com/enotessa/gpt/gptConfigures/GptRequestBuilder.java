package com.enotessa.gpt.gptConfigures;

import com.enotessa.gpt.GptMessage;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.util.List;

public interface GptRequestBuilder {
    JSONObject createBody(List<GptMessage> messagesArray);
    HttpRequest createRequest(JSONObject body);
}
