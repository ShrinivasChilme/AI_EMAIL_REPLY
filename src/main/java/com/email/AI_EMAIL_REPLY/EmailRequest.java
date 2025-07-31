package com.email.AI_EMAIL_REPLY;

import lombok.Data;

@Data
public class EmailRequest {
    private String emailContent;
    private String tone;
}
