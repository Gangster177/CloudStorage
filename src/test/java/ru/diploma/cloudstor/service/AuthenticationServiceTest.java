package ru.diploma.cloudstor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.diploma.cloudstor.repository.AuthenticationRepository;
import ru.diploma.cloudstor.security.JwtTokenUtil;
import ru.diploma.cloudstor.web.response.AuthResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.diploma.cloudstor.DataTest.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("=== Testing authentication service ===")
public class AuthenticationServiceTest {


    @InjectMocks
    AuthenticationService authenticationService;

    @Mock
    AuthenticationRepository authenticationRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserService userService;

    @Mock
    JwtTokenUtil jwtTokenUtil;


    @Test
    void loginTest() {
        when(userService.loadUserByUsername(USERNAME)).thenReturn(USER_DETAILS);
        when(jwtTokenUtil.generateToken(USER_DETAILS)).thenReturn(TOKEN);

        AuthResponse expected = RESPONSE;
        AuthResponse result = authenticationService.login(REQUEST);
        assertEquals(expected, result);
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD));
        Mockito.verify(authenticationRepository, Mockito.times(1)).putTokenAndUsername(TOKEN, USERNAME);
    }

    @Test
    void logoutTest() {
        when(authenticationRepository.getUserNameByToken(BEARER_TOKEN.substring(7))).thenReturn(USERNAME);
        authenticationService.logout(BEARER_TOKEN);
        Mockito.verify(authenticationRepository, Mockito.times(1)).getUserNameByToken(BEARER_TOKEN.substring(7));
        Mockito.verify(authenticationRepository, Mockito.times(1)).removeTokenAndUsernameByToken(BEARER_TOKEN.substring(7));
    }
}
