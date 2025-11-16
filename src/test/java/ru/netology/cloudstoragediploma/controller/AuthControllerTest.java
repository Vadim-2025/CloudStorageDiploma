package ru.netology.cloudstoragediploma.controller;

import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.model.Token;
import ru.netology.cloudstoragediploma.service.AuthService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer auth-token";
    private static final String LOGIN = "testLogin@test.ru";
    private static final String PASSWORD = "testPassword";

    private MockMvc mockMvc;
    private AuthService authService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void test_authenticationLogin() throws Exception {
        UserDTO userDTO = UserDTO.builder().login(LOGIN).password(PASSWORD).build();

        Token token = new Token(AUTH_TOKEN);

        Mockito.when(authService.login(userDTO)).thenReturn(token);

        mockMvc.perform(post("/login")
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void test_logout() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        Mockito.when(request.getHeader(AUTH_TOKEN)).thenReturn(AUTH_TOKEN);

        AuthController controller = new AuthController(authService);

        Assertions.assertDoesNotThrow(() -> controller.logout(AUTH_TOKEN, request, response));
        Mockito.verify(authService, Mockito.times(1)).logout(AUTH_TOKEN, request, response);
    }
}