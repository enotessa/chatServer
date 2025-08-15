package com.enotessa.gpt.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String sender;
    private String message;
}
