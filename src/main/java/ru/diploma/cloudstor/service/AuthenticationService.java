package ru.diploma.cloudstor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.diploma.cloudstor.entity.Role;
import ru.diploma.cloudstor.entity.User;
import ru.diploma.cloudstor.exception.InputDataException;
import ru.diploma.cloudstor.model.EnumRoles;
import ru.diploma.cloudstor.repository.AuthenticationRepository;
import ru.diploma.cloudstor.repository.UserRepository;
import ru.diploma.cloudstor.security.JwtTokenUtil;
import ru.diploma.cloudstor.web.request.AuthRequest;
import ru.diploma.cloudstor.web.response.AuthResponse;

import java.util.Collections;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationRepository authenticationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //здесь мы регистрируем нового пользователя и сохраняем его в базу, если такая функция будет необходима
    public ResponseEntity<?> register(AuthRequest request) {
        Optional<User> userFromBD = userRepository.findByUsername(request.getLogin());
        if (userFromBD.isPresent()) {
            throw new InputDataException("User with the same username already exists");
        }

        User newUser = User.builder()
                .username(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(new Role(EnumRoles.ROLE_USER)))
                .build();
        userRepository.save(newUser);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    public AuthResponse login(AuthRequest request) {
        final String username = request.getLogin();
        final String password = request.getPassword();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final UserDetails userDetails = userService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);
        authenticationRepository.putTokenAndUsername(token, username);
        log.info("User {} is authorized", username);
        return AuthResponse.builder()
                .authToken(token)
                .build();
    }


    public void logout(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        final String username = authenticationRepository.getUsernameByToken(authToken);
        log.info("User {} logout", username);
        authenticationRepository.removeTokenAndUsernameByToken(authToken);
    }
}
