package ru.netology.cloudstoragediploma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.enums.Role;
import ru.netology.cloudstoragediploma.exception.UserAlreadyExistsException;
import ru.netology.cloudstoragediploma.exception.UserNotFoundException;
import ru.netology.cloudstoragediploma.repository.UserRepository;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрация нового пользователя.
     *
     * @param user Объект пользователя
     * @return Зарегистрированный пользователь
     */
    public User regUser(User user) {
        log.info("Регистрация нового пользователя {}", user.getLogin());

        // Проверяем, не существует ли пользователь с таким логином
        userRepository.findUserByLogin(user.getLogin()).ifPresent(s -> {
            log.warn("Пользователь с данным логином уже существует");
            throw new UserAlreadyExistsException("User already exists", user.getId());
        });

        // Шифруем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Устанавливаем начальные роли (обычно ROLE_USER)
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setRole(Role.ROLE_USER.getAuthority());

        // Сохраняем пользователя в базу данных
        return userRepository.save(user);
    }

    /**
     * Получение информации о пользователе по идентификатору.
     *
     * @param id Идентификатор пользователя
     * @return Пользователь
     */
    public User getUser(Long id) {
        log.info("Получение информации о пользователе с id={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден", id));
    }

    /**
     * Удаление пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя
     */
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id={}", id);
        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден", id));
        userRepository.delete(foundUser);
    }
}