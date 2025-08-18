package com.enotessa.gpt;

import com.enotessa.gpt.enums.ProfessionEnum;
import com.enotessa.gpt.gptConfigures.GptRequestBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class GptService {
    @Autowired
    private GptRequestBuilder gptRequestBuilder;
    private ProfessionEnum profession;

    GptService() {
        profession = ProfessionEnum.JAVA_MIDDLE;
    }

    public String sendChatRequest(String message, List<GptMessage> messagesArray) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            JSONObject body = gptRequestBuilder.createBody(messagesArray);
            HttpRequest request = gptRequestBuilder.createRequest(body);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            return json.getJSONObject("result")
                    .getJSONArray("alternatives")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("text");
        } catch (Exception e) {
            throw new RuntimeException("Error sending request to OpenAI API: " + e.getMessage(), e);
        }
    }

    public void changeInterviewProfession(String profession) {
        this.profession = ProfessionEnum.fromLabel(profession);
    }
}
