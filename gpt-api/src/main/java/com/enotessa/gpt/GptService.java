package com.enotessa.gpt;

import com.enotessa.gpt.enums.ProfessionEnum;
import com.enotessa.gpt.exceptions.GptApiException;
import com.enotessa.gpt.gptConfigures.GptRequestBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GptService {
    private static final Logger logger = LoggerFactory.getLogger(GptService.class);
    private final GptRequestBuilder gptRequestBuilder;
    @Getter
    private ProfessionEnum profession;
    private final HttpClient httpClient;

    private static final String OPENAI_API_ERROR = "Error sending request to OpenAI API: ";
    private static final String JSON_RESULT_KEY = "result";
    private static final String JSON_ALTERNATIVES_KEY = "alternatives";
    private static final String JSON_MESSAGE_KEY = "message";
    private static final String JSON_TEXT_KEY = "text";

    private static final String MISSING_RESULT_KEY = "Missing 'result' key in API response";
    private static final String MISSING_ALTERNATIVES_KEY = "Missing 'alternatives' key in API response";
    private static final String NO_ALTERNATIVES_FOUND = "No alternatives found in API response";
    private static final String MISSING_MESSAGE_KEY = "Missing 'message' key in API response";
    private static final String MISSING_TEXT_KEY = "Missing 'text' key in API response";

    @Autowired
    public GptService(GptRequestBuilder gptRequestBuilder) {
        this.gptRequestBuilder = gptRequestBuilder;
        this.httpClient = HttpClient.newHttpClient();
        this.profession = ProfessionEnum.JAVA_MIDDLE;
    }

    public CompletableFuture<String> sendChatRequest(@NonNull String message, @NonNull List<GptMessage> messagesArray) {
        try {
            JSONObject body = gptRequestBuilder.createBody(messagesArray);
            HttpRequest request = gptRequestBuilder.createRequest(body);
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        JSONObject json = new JSONObject(response.body());
                        return parseResponse(json);
                    })
                    .exceptionally(throwable -> {
                        logger.error("Error sending request to OpenAI API: {}", throwable.getMessage(), throwable);
                        throw new GptApiException(OPENAI_API_ERROR + throwable.getMessage(), throwable);
                    });
        } catch (Exception e) {
            logger.error("Error preparing request to OpenAI API: {}", e.getMessage(), e);
            throw new GptApiException(OPENAI_API_ERROR + e.getMessage(), e);
        }
    }

    public void changeInterviewProfession(@NonNull String profession) {
        try {
            this.profession = ProfessionEnum.fromLabel(profession);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid profession label: {}", profession);
            throw new IllegalArgumentException("Invalid profession label: " + profession, e);
        }
    }

    private String parseResponse(JSONObject json) {
        if (!json.has(JSON_RESULT_KEY)) {
            logger.error(MISSING_RESULT_KEY);
            throw new GptApiException(MISSING_RESULT_KEY);
        }
        JSONObject result = json.getJSONObject(JSON_RESULT_KEY);

        if (!result.has(JSON_ALTERNATIVES_KEY)) {
            logger.error(MISSING_ALTERNATIVES_KEY);
            throw new GptApiException(MISSING_ALTERNATIVES_KEY);
        }
        JSONArray alternatives = result.getJSONArray(JSON_ALTERNATIVES_KEY);

        if (alternatives.isEmpty()) {
            logger.error(NO_ALTERNATIVES_FOUND);
            throw new GptApiException(NO_ALTERNATIVES_FOUND);
        }
        JSONObject firstAlternative = alternatives.getJSONObject(0);

        if (!firstAlternative.has(JSON_MESSAGE_KEY)) {
            logger.error(MISSING_MESSAGE_KEY);
            throw new GptApiException(MISSING_MESSAGE_KEY);
        }
        JSONObject messageObj = firstAlternative.getJSONObject(JSON_MESSAGE_KEY);

        if (!messageObj.has(JSON_TEXT_KEY)) {
            logger.error(MISSING_TEXT_KEY);
            throw new GptApiException(MISSING_TEXT_KEY);
        }
        return messageObj.getString(JSON_TEXT_KEY);
    }
}
