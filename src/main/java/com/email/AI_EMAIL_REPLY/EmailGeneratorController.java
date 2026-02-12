package com.email.AI_EMAIL_REPLY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmailGeneratorController {
    private static final Logger logger = LoggerFactory.getLogger(EmailGeneratorController.class);
    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
        logger.info("==================== NEW REQUEST ====================");
        logger.info("POST /api/email/generate endpoint called");
        logger.info("Email content length: {} characters", emailRequest.getEmailContent() != null ? emailRequest.getEmailContent().length() : 0);
        logger.info("Email tone: {}", emailRequest.getTone());
        
        try {
            String response = emailGeneratorService.generateEmailReply(emailRequest);
            logger.info("Service returned response (length: {} chars)", response.length());
            
            if (response.startsWith("Error")) {
                logger.warn("Service returned an error: {}", response);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } else if (response.startsWith("Unexpected response structure")) {
                logger.warn("Service returned unexpected response structure: {}", response);
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
            } else {
                logger.info("Success! Returning generated email reply");
                logger.debug("Generated reply: {}", response);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Exception in controller", e);
            String errorMsg = "Internal server error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }
}
