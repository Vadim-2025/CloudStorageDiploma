package ru.netology.cloudstoragediploma.service;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.model.Token;
import ru.netology.cloudstoragediploma.repository.UserRepository;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.security.JwtProvider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest
public class AuthServiceTest {
    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer token";
    @Autowired
    private AuthService authService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private JwtProvider jwtProvider;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        userDTO = UserDTO.builder()
                .login("testLogin@test.ru")
                .password("testPassword")
                .build();
        user = User.builder()
                .id(1L)
                .login("testLogin@test.ru")
                .password("testPassword")
                .build();
    }

    @Test
    public void test_authenticationLogin() {
        Mockito.when(userRepository.findUserByLogin(userDTO.getLogin())).thenReturn(Optional.ofNullable(user));
        Mockito.when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(jwtProvider.generateAccessToken(user)).thenReturn(VALUE_TOKEN);

        Token generatedToken = authService.login(userDTO);
        Assertions.assertNotNull(generatedToken);
    }

    @Test
    @WithMockUser(username = "testLogin@test.ru",password = "testPassword")
    public void test_logout() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Mockito.when(userRepository.findUserByLogin(auth.getName())).thenReturn(Optional.ofNullable(user));
        String login = authService.logout(AUTH_TOKEN, request, response);
        Assertions.assertEquals(login, userDTO.getLogin());
    }
}