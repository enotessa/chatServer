package com.enotessa.gpt.gptConfigures;

import com.enotessa.gpt.GptMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

@Component
@ConditionalOnProperty(name = "gpt.name", havingValue = "yandex")
public class YandexGptRequestBuilder implements GptRequestBuilder {
    @Value("${gpt.yandex.folder-id}")
    private String folderId;
    @Value("${gpt.yandex.key}")
    private String apiKey;
    @Value("${gpt.yandex.api-url}")
    private String apiUrl;
    @Value("${gpt.yandex.gpt-model}")
    private String gptModel;

    private boolean stream = false;
    private double temperature = 0.6;
    private String maxTokens = "2000";
    private String mode = "DISABLED";

    @Override
    public JSONObject createBody(List<GptMessage> messagesArray) {
        return new JSONObject()
                .put("modelUri", "gpt://" + folderId + "/" + gptModel + "/latest")
                .put("completionOptions", createCompletionOptions())
                .put("messages", createJsonMessagesArray(messagesArray));
    }

    private JSONObject createCompletionOptions() {
        return new JSONObject()
                .put("stream", stream)
                .put("temperature", temperature)
                .put("maxTokens", maxTokens)
                .put("reasoningOptions", new JSONObject()
                        .put("mode", mode));
    }

    private JSONArray createJsonMessagesArray(List<GptMessage> messagesArray) {
        JSONArray jsonMessagesArray = new JSONArray();
        messagesArray.forEach(message -> jsonMessagesArray.put(new JSONObject()
                .put("role", message.getRole())
                .put("text", message.getMessage())));
        return jsonMessagesArray;
    }

    @Override
    public HttpRequest createRequest(JSONObject body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Api-Key " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }
}
