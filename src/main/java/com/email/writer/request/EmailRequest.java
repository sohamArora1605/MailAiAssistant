package com.email.writer.request;

import lombok.Data;

@Data
public class EmailRequest {
    private String content;
    private String taskType;        // "summarise", "reply", or "translate"
    private String tone;            // used if taskType == "reply"
    private String targetLanguage;  // used if taskType == "translate"
}
