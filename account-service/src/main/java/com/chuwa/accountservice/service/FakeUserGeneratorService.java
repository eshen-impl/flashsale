package com.chuwa.accountservice.service;

public interface FakeUserGeneratorService {
    String signUpFakeUsers(int start, int end);
    String generateFakeTokenForUser(int start, int end);
}
