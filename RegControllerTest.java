package ru.netology.cloudstoragediploma.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.service.RegService;
import ru.netology.cloudstoragediploma.utils.MapUtils;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegControllerTest {
    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer auth-token";
    private static final String LOGIN = "testLogin@test.ru";
    private static final String PASSWORD = "testPassword";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RegService regService;
    private MapUtils mapUtils;

    @BeforeEach
    void setUp() {
        regService = mock(RegService.class);
        mapUtils = mock(MapUtils.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RegController(regService, mapUtils))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void test_registerUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        // Моделируем процесс преобразования DTO в сущность
        User user = User.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        // Регистрация пользователя
        User regUser = User.builder()
                .id(1L)
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        // Когда происходит регистрация, возвращаем зарегистированного пользователя
        Mockito.when(mapUtils.toUserEntity(userDTO)).thenReturn(user);
        Mockito.when(regService.regUser(user)).thenReturn(regUser);

        // После регистрации ожидаем обратное преобразование в DTO
        UserDTO expectedDTO = mapUtils.toUserDto(regUser);
        Mockito.when(mapUtils.toUserDto(regUser)).thenReturn(expectedDTO);

        // Отправляем POST-запрос
        mockMvc.perform(post("/user/register")
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value(LOGIN));
    }

    @Test
    void test_getUser() throws Exception {
        User user = User.builder()
                .id(2L)
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        // Переводим сущность в DTO
        UserDTO userDTO = UserDTO.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        // Когда запрашиваем пользователя, возвращаем сущность
        Mockito.when(regService.getUser(2L)).thenReturn(user);
        Mockito.when(mapUtils.toUserDto(user)).thenReturn(userDTO);

        // Отправляем GET-запрос
        mockMvc.perform(get("/user/{id}", "2")
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value(LOGIN));
    }

    @Test
    void test_deleteUser() throws Exception {
        // Просто проверяем, что DELETE-запрос прошёл успешно
        mockMvc.perform(delete("/user/delete/{id}", "2")
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isNoContent());
    }
}