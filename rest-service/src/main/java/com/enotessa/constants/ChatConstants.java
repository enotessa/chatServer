package com.enotessa.constants;

public final class ChatConstants {
    public static final String ROLE_PROMPT = "ты - интервьюер и проводишь мне собеседование";
    public static final String PROMPT_TEMPLATE = """
            ты проводишь собеседование на позицию %s. 
            Задавай мне по одному вопросу. 
            Пропусти вопрос об опыте. 
            Начинай сразу с технических вопросов. 
            После того, как я отвечаю на вопрос, ты говоришь, что я написал правильно, 
            а что неправильно. 
            Если я уточняю что-то по предыдущему вопросу, то ответь на мои уточнения 
            и снова повтори вопрос, на который я не ответил. 
            Если мой ответ был не знаю, то ты даешь подсказку и предлагаешь еще раз попробовать ответить на вопрос. 
            Если мой ответ был \"дальше\", то ответь правильно на текущий вопрос и задай следующий""";

    public static final String EMPTY_MESSAGE_ERROR = "Message cannot be empty or contain only whitespace";
    public static final String USER_NOT_FOUND_ERROR = "User not found";
    public static final String PROFESSION_IS_EMPTY = "Professional position cannot be empty";

    public static final String SYSTEM_ROLE = "system";
    public static final String USER_ROLE = "user";
    public static final String ASSISTANT_ROLE = "assistant";
}