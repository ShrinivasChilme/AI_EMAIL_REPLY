package com.email.AI_EMAIL_REPLY;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Map;
import java.util.List;

@Service
public class EmailGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(EmailGeneratorService.class);
    private final WebClient webClient;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.model}")
    private String groqModel;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest) {
        logger.info("========== EMAIL GENERATION REQUEST STARTED ==========");
        logger.info("Received email request with tone: {}", emailRequest.getTone());
        logger.info("Email content length: {} characters", emailRequest.getEmailContent() != null ? emailRequest.getEmailContent().length() : 0);
        
        try {
            String prompt = buildPrompt(emailRequest);
            logger.debug("Built prompt: {}", prompt);
            
            // Groq API uses OpenAI-compatible format with messages
            Map<String, Object> requestBody = Map.of(
                    "model", groqModel,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    ),
                    "max_tokens", 1000,
                    "temperature", 0.7
            );
            
            logger.info("Making API call to: {}", groqApiUrl);
            logger.debug("Using model: {}", groqModel);
            logger.debug("Request body: {}", new ObjectMapper().writeValueAsString(requestBody));
            
            logger.info("Calling Groq API...");
            String response = webClient.post()
                    .uri(groqApiUrl)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Received response from Groq API (length: {} chars)", response != null ? response.length() : 0);
            logger.debug("Raw API response: {}", response);
            
            String result = extractResponseContent(response);
            logger.info("========== EMAIL GENERATION REQUEST COMPLETED SUCCESSFULLY ==========");
            return result;
            
        } catch (WebClientResponseException e) {
            logger.error("WebClientResponseException - Status Code: {}", e.getStatusCode());
            logger.error("Response Body: {}", e.getResponseBodyAsString());
            String errorMessage = "Error calling Groq API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
            logger.error("Returning error: {}", errorMessage);
            return errorMessage;
        } catch (Exception e) {
            logger.error("Unexpected exception occurred", e);
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Exception type: {}", e.getClass().getName());
            String errorMessage = "Error processing request: " + e.getMessage();
            logger.error("Returning error: {}", errorMessage);
            return errorMessage;
        }
    }

    private String extractResponseContent(String response) {
        logger.info("Extracting content from response...");
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            logger.debug("Root node parsed successfully");
            
            // Groq uses OpenAI format: response.choices[0].message.content
            JsonNode choices = rootNode.path("choices");
            logger.info("Choices array size: {}", choices.size());
            
            if (choices.isArray() && choices.size() > 0) {
                JsonNode textNode = choices.get(0)
                        .path("message")
                        .path("content");
                
                if (!textNode.isMissingNode()) {
                    String extractedText = textNode.asText();
                    logger.info("Successfully extracted text (length: {} chars)", extractedText.length());
                    return extractedText;
                } else {
                    logger.warn("Content node is missing");
                }
            } else {
                logger.warn("No choices found in response or choices is not an array");
            }
            
            String errorMsg = "Unexpected response structure: " + response;
            logger.error(errorMsg);
            return errorMsg;
        } catch (Exception e) {
            logger.error("Error parsing response", e);
            logger.error("Exception during parsing: {}", e.getMessage());
            String errorMessage = "Error parsing response: " + e.getMessage() + ". Raw response: " + response;
            logger.error(errorMessage);
            return errorMessage;
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please do not generate a subject line. ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        logger.debug("Prompt built with {} characters", prompt.length());
        return prompt.toString();
    }
}
