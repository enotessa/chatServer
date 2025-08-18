package com.enotessa.gpt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GptMessage {
    private String role;
    private String message;
}
