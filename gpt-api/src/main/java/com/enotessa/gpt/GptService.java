package com.enotessa.gpt;

import com.enotessa.gpt.enums.ProfessionEnum;
import com.enotessa.gpt.gptConfigures.GptRequestBuilder;
import com.enotessa.gpt.objects.Message;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class GptService {
    @Autowired
    private GptRequestBuilder gptRequestBuilder;
    private ProfessionEnum profession;
    private List<Message> messagesArray = new ArrayList<>();

    String ROLE_PROMT = "ты - интервьюер и проводишь мне собеседование";
    String PROMT = """
            ты проводишь собеседование на позицию %s.\s
            Задавай мне по одному вопросу.\s
            Пропусти вопрос об опыте.\s
            Начинай сразу с технических вопросов.\s
            После того, как я отвечаю на вопрос, ты говоришь, что я написал правильно,\s
            а что направильно и задаешь следующий вопрос""";

    private String SYSTEM_ROLE = "system";
    private String USER_ROLE = "user";
    private String ASSISTANT_ROLE = "assistant";

    GptService() {
        profession = ProfessionEnum.JAVA_MIDDLE;
    }

    public String sendChatRequest(String message) {
        try {
            //TODO сделать нормальную реализацию с БД для messagesArray. пока что просто заглушка
            messagesArray.add(new Message(USER_ROLE, message));

            HttpClient client = HttpClient.newHttpClient();
            JSONObject body = gptRequestBuilder.createBody(messagesArray);
            HttpRequest request = gptRequestBuilder.createRequest(body);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            String answer = json.getJSONObject("result")
                    .getJSONArray("alternatives")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("text");
            messagesArray.add(new Message(ASSISTANT_ROLE, answer));

            return answer;
        } catch (Exception e) {
            throw new RuntimeException("Error sending request to OpenAI API: " + e.getMessage(), e);
        }
    }

    public void changeInterviewProfession(String profession) {
        this.profession = ProfessionEnum.fromLabel(profession);
        messagesArray.clear();
        messagesArray.add(new Message(SYSTEM_ROLE, ROLE_PROMT));
        messagesArray.add(new Message(USER_ROLE, String.format(PROMT, profession)));
    }
}
