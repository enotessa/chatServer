package com.enotessa.gpt.gptConfigures;

import com.enotessa.gpt.objects.Message;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.util.List;

public interface GptRequestBuilder {
    JSONObject createBody(List<Message> messagesArray);
    HttpRequest createRequest(JSONObject body);
}
