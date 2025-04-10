package com.chuwa.accountservice.service.impl;

import com.chuwa.accountservice.service.FakeUserGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.util.*;

@Slf4j
@Service
public class FakeUserGeneratorServiceImpl implements FakeUserGeneratorService {
    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private static final String SIGN_UP_URL = BASE_URL + "/auth/sign-up";
    private static final String SIGN_IN_URL = BASE_URL + "/auth/sign-in";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String signUpFakeUsers(int start, int end) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        int counter = 0;
        for (int i = start; i <= end; i++) {
            String email = "user" + i + "@test.com";
            String password = "Pass123!";
            String username = "user" + i;

            Map<String, String> signUpRequest = Map.of(
                    "email", email,
                    "password", password,
                    "username", username
            );

            HttpEntity<Map<String, String>> signUpEntity = new HttpEntity<>(signUpRequest, headers);

            try {
                restTemplate.exchange(SIGN_UP_URL, HttpMethod.POST, signUpEntity, String.class);
                counter++;
            } catch (Exception e) {
                log.warn("Sign-up failed for: {}, reason: {}", username, e.getMessage());
            }
        }
        return "Successfully signed up  " + counter + " users.";

    }

    public String generateFakeTokenForUser(int start, int end) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<String> tokens = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            String email = "user" + i + "@test.com";
            String password = "Pass123!";

            Map<String, String> signInRequest = Map.of(
                    "email", email,
                    "password", password
            );

            HttpEntity<Map<String, String>> signInEntity = new HttpEntity<>(signInRequest, headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(SIGN_IN_URL, HttpMethod.POST, signInEntity, String.class);
                Map<String, String> body = objectMapper.readValue(response.getBody(), Map.class);
                tokens.add(body.get("token"));
            } catch (Exception e) {
                log.warn("Sign-in failed for: {}, reason: {}", email, e.getMessage());
            }
        }

        try (FileWriter writer = new FileWriter("/app/output/tokens.txt")) {
            for (String token : tokens) {
                writer.write(token + "\n");
            }
        } catch (Exception e) {
            return "Failed to write tokens to file.";
        }

        return "Successfully generated " + tokens.size() + " tokens.";
    }


}



