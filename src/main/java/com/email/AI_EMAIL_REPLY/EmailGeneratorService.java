package com.email.AI_EMAIL_REPLY;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
//
//import java.util.Map;
//
//@Service
//public class EmailGeneratorService {
//
//    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.build();
//    }
//
//    private final WebClient webClient;
//    @Value("${gemini.api.url}")
//    private String geminiApiUrl;
//    @Value("${gemini.api.key}")
//    private String getGeminiApiKey;
//    public String generateEmailReply(EmailRequest emailRequest){
//        String prompt=buildPrompt(emailRequest);
//        Map<String,Object> requestBody= Map.of(
//                "contents",new Object[]{
//                        Map.of("parts",new Object[]{
//                            Map.of("text",prompt)
//                })
//                }
//        );
//       String response=webClient.post()
//               .uri(geminiApiUrl+getGeminiApiKey)
//               .header("Content-Type","application/json")
//               .bodyValue(requestBody)
//               .retrieve()
//               .bodyToMono(String.class)
//               .block();
//
//       return extractResponseContent(response);
//    }
//
//    private String extractResponseContent(String response) {
//        try{
//            ObjectMapper mapper=new ObjectMapper();
//            JsonNode rootNode=mapper.readTree(response);
//            return rootNode.path("candidates")
//                    .get(0)
//                    .path("content")
//                    .path("parts")
//                    .get(0)
//                    .path("text")
//                    .asText();
//        }
//        catch (Exception e){
//            return "Error processing request: "+e.getMessage();
//        }
//    }
//
//    private String buildPrompt(EmailRequest emailRequest) {
//        StringBuilder prompt=new StringBuilder();
//        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line");
//        if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty()){
//            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
//        }
//        prompt.append("\n Original email: \n").append(emailRequest.getEmailContent());
//        return prompt.toString();
//    }
//}










//
//package com.email.AI_EMAIL_REPLY;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//import java.util.Map;
//
//@Service
//public class EmailGeneratorService {
//
//    private final WebClient webClient;
//
//    @Value("${gemini.api.url}")
//    private String geminiApiUrl;
//
//    @Value("${gemini.api.key}")
//    private String geminiApiKey;
//
//    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.build();
//    }
//
//    public String generateEmailReply(EmailRequest emailRequest) {
//        try {
//            String prompt = buildPrompt(emailRequest);
//            Map<String, Object> requestBody = Map.of(
//                    "contents", new Object[]{
//                            Map.of("parts", new Object[]{
//                                    Map.of("text", prompt)
//                            })
//                    }
//            );
//
//            // Construct the full URL with API key as query parameter
//            String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;
//
//            String response = webClient.post()
//                    .uri(fullUrl)
//                    .header("Content-Type", "application/json")
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            return extractResponseContent(response);
//        } catch (WebClientResponseException e) {
//            return "Error calling Gemini API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
//        } catch (Exception e) {
//            return "Error processing request: " + e.getMessage();
//        }
//    }
//
//    private String extractResponseContent(String response) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode rootNode = mapper.readTree(response);
//
//            // Check if the response has the expected structure
//            if (rootNode.has("candidates") && rootNode.get("candidates").size() > 0) {
//                JsonNode candidate = rootNode.get("candidates").get(0);
//                if (candidate.has("content") && candidate.get("content").has("parts")
//                        && candidate.get("content").get("parts").size() > 0) {
//                    return candidate.get("content").get("parts").get(0).get("text").asText();
//                }
//            }
//
//            // If structure is different, return the raw response for debugging
//            return "Unexpected response structure: " + response;
//        } catch (Exception e) {
//            return "Error parsing response: " + e.getMessage() + ". Raw response: " + response;
//        }
//    }
//
//    private String buildPrompt(EmailRequest emailRequest) {
//        StringBuilder prompt = new StringBuilder();
//        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line. ");
//
//        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
//            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
//        }
//
//        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
//        return prompt.toString();
//    }
//}
@Service
public class EmailGeneratorService {

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String getGeminiApiKey;

    public String generateEmailReply(EmailRequest emailRequest) {
        String prompt = buildPrompt(emailRequest);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        String response = webClient.post()
                .uri(geminiApiUrl)
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", getGeminiApiKey) // ✅ Correct way
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return "Error processing request: " + e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line. ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
