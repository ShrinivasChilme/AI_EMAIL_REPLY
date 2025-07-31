package com.email.AI_EMAIL_REPLY;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailGeneratorController {

    private final EmailGeneratorService emailGeneratorService;
    @PostMapping("/generate")
    public ResponseEntity<String>generateEmail(@RequestBody EmailRequest emailRequest){
        String response=emailGeneratorService.generateEmailReply(emailRequest);

        return ResponseEntity.ok(response);
    }
}
















//
//package com.email.AI_EMAIL_REPLY;
//
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/email")
//@AllArgsConstructor
//@CrossOrigin(origins = "*")
//public class EmailGeneratorController {
//
//    private final EmailGeneratorService emailGeneratorService;
//
//    @PostMapping("/generate")
//    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
//        try {
//            // Validate input
//            if (emailRequest.getEmailContent() == null || emailRequest.getEmailContent().trim().isEmpty()) {
//                return ResponseEntity.badRequest().body("Email content cannot be empty");
//            }
//
//            String response = emailGeneratorService.generateEmailReply(emailRequest);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error generating email: " + e.getMessage());
//        }
//    }
//}