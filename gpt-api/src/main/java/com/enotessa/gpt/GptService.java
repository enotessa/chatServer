package com.enotessa.gpt;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GptService {
    @Value("${ai-api.folder-id}")
    private String folderId;
    @Value("${ai-api.key}")
    private String apiKey;
    @Value("${ai-api.api-url}")
    private String apiUrl;
    @Value("${ai-api.gpt-model}")
    private String gptModel;

    public String sendChatRequest(String message) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject body = new JSONObject()
                    .put("modelUri", "gpt://" + folderId + "/" + gptModel + "/latest")
                    .put("completionOptions", new JSONObject()
                            .put("stream", false)
                            .put("temperature", 0.6)
                            .put("maxTokens", "2000")
                            .put("reasoningOptions", new JSONObject()
                                    .put("mode", "DISABLED")))
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "system")
                                    .put("text", "ты проводишь собеседование на позицию" + message))
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("text", message)));
            /*TODO вынести в отдельную переменную массив для messages,
               сделать отдельный метод для смены позиции, по которой будет проводиться собес,
                сохранять историю в БД*/

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Api-Key " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            String answer = json.getJSONObject("result")
                    .getJSONArray("alternatives")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("text");
            /*TODO добавлять answer в массив с сообщениями messages*/
            return answer;
        } catch (Exception e) {
            throw new RuntimeException("Error sending request to OpenAI API: " + e.getMessage(), e);
        }
    }
}
