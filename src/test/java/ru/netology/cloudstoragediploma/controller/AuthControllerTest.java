package ru.netology.cloudstoragediploma.controller;

import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.model.Token;

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
import ru.netology.cloudstoragediploma.service.AuthService;
import ru.netology.cloudstoragediploma.utils.MapUtils;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer auth-token";
    private static final String LOGIN = "testLogin@test.ru";
    private static final String PASSWORD = "testPassword";

    private MockMvc mockMvc;
    private AuthService authService;
    private MapUtils mapUtils;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mapUtils = mock(MapUtils.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService, mapUtils))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void test_authenticationLogin() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        // Преобразуем DTO в сущность User
        User user = mapUtils.toUserEntity(userDTO);

        // Создание токена
        Token token = new Token(AUTH_TOKEN);

        // Мокируем работу службы авторизации
        Mockito.when(mapUtils.toUserEntity(userDTO)).thenReturn(user);
        Mockito.when(authService.login(user)).thenReturn(token);

        // Отправляем POST-запрос
        mockMvc.perform(post("/login")
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.auth-token").value(AUTH_TOKEN));
    }

    @Test
    public void test_logout() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Подготовка запроса
        Mockito.when(request.getHeader(AUTH_TOKEN)).thenReturn(AUTH_TOKEN);

        // Операция логаута
        AuthController controller = new AuthController(authService, mapUtils);
        Assertions.assertDoesNotThrow(() -> controller.logout(AUTH_TOKEN, request, response));
        Mockito.verify(authService, Mockito.times(1)).logout(AUTH_TOKEN, request, response);
    }
}