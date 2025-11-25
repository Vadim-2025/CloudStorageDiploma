package ru.netology.cloudstoragediploma.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstoragediploma.dto.UserDTO;

import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.service.RegService;
import ru.netology.cloudstoragediploma.utils.MapUtils;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class RegController {
    private final RegService regService;
    private final MapUtils mapUtils;

    /**
     * Регистрация нового пользователя.
     *
     * @param userDTO Данные пользователя в формате DTO
     * @return Зарегистрированный пользователь в формате DTO
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> regUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Регистрация нового пользователя {}", userDTO.getLogin());

        // Преобразуем объект DTO в сущность User
        User user = mapUtils.toUserEntity(userDTO);

        // Регистрируем пользователя и получаем результат
        User regUser = regService.regUser(user);

        // Конвертируем полученный объект User обратно в DTO
        UserDTO dto = mapUtils.toUserDto(regUser);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /**
     * Получение информации о пользователе по идентификатору.
     *
     * @param id Идентификатор пользователя
     * @return Информация о пользователе в формате DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        log.info("Запрашиваем информацию о пользователе с id={}", id);

        // Получаем пользователя из сервиса
        User user = regService.getUser(id);

        // Преобразуем в DTO
        UserDTO dto = mapUtils.toUserDto(user);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Удаление пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Удаляем пользователя с id={}", id);
        regService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}