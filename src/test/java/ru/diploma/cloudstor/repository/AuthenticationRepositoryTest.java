package ru.diploma.cloudstor.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static ru.diploma.cloudstor.DataTest.*;

@SpringBootTest
@DisplayName("=== Testing authentication repository ===")
public class AuthenticationRepositoryTest {
    @Autowired
    AuthenticationRepository authenticationRepository;
    private final Map<String, String> testTokens = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        authenticationRepository = new AuthenticationRepository();
        authenticationRepository.putTokenAndUsername(TOKEN_1, USERNAME_1);
        testTokens.clear();
        testTokens.put(TOKEN_1, USERNAME_1);
    }

    @Test
    void putTokenAndUsernameTest() {
        String beforePut = authenticationRepository.getUsernameByToken(TOKEN_2);
        assertNull(beforePut);
        authenticationRepository.putTokenAndUsername(TOKEN_2, USERNAME_2);
        String afterPut = authenticationRepository.getUsernameByToken(TOKEN_2);
        assertEquals(USERNAME_2, afterPut);
    }

    @Test
    void getUsernameByToken() {
        String tokenActual = authenticationRepository.getUsernameByToken(TOKEN_1);
        assertEquals(testTokens.get(TOKEN_1), tokenActual);
    }

    @Test
    void removeTokenAndUsernameByToken() {
        String beforeRemove = authenticationRepository.getUsernameByToken(TOKEN_1);
        assertNotNull(beforeRemove);
        authenticationRepository.removeTokenAndUsernameByToken(TOKEN_1);
        String afterRemove = authenticationRepository.getUsernameByToken(TOKEN_1);
        assertNull(afterRemove);
    }
}
