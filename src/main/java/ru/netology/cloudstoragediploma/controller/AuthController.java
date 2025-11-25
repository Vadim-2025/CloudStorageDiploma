package ru.netology.cloudstoragediploma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.model.Token;
import ru.netology.cloudstoragediploma.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.netology.cloudstoragediploma.utils.MapUtils;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MapUtils mapUtils;

    /**
     * Авторизация пользователя.
     *
     * @param userDTO Входящие данные пользователя в формате DTO
     * @return Токен доступа
     */
    @PostMapping("/login")
    public Token login(@RequestBody UserDTO userDTO) {
        try {
            log.info("Попытка входа пользователя {}", userDTO.getLogin());

            // Преобразуем объект DTO в сущность User
            User user = mapUtils.toUserEntity(userDTO);

            // Получаем токен через службу авторизации
            Token token = authService.login(user);
            log.info("Пользователь {} вошёл успешно", userDTO.getLogin());
            return token;
        } catch (Exception e) {
            log.error("Ошибка при входе пользователя {}", userDTO.getLogin(), e);
            throw e;
        }
    }

    /**
     * Выход пользователя.
     *
     * @param authToken Токен аутентификации
     * @param request   Запрос
     * @param response  Ответ
     * @return Результат операции
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("auth-token") String authToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            log.info("Попытка выхода пользователя с токеном {}", authToken);
            String result = authService.logout(authToken, request, response);
            if (result == null) {
                log.warn("Выход пользователя неуспешен");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            log.info("Пользователь вышел успешно");
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Ошибка при выходе пользователя", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}