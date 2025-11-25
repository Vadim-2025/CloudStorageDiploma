package ru.netology.cloudstoragediploma.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.exception.InvalidInputDataException;
import ru.netology.cloudstoragediploma.exception.UserNotFoundException;
import ru.netology.cloudstoragediploma.model.Token;
import ru.netology.cloudstoragediploma.repository.UserRepository;
import ru.netology.cloudstoragediploma.security.JwtProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Авторизация пользователя.
     *
     * @param user Пользовательская сущность
     * @return Токен доступа
     */
    public Token login(@NonNull User user) {
        try {
            log.debug("Ищем пользователя в БД по логину {}", user.getLogin());
            User userFromDB = findUserInStorage(user.getLogin());
            if (isEquals(user, userFromDB)) {
                String accessToken = jwtProvider.generateAccessToken(userFromDB);
                log.info("Токен успешно сгенерирован для пользователя {}", user.getLogin());
                return new Token(accessToken);
            }
            log.warn("Пароль неверный для пользователя {}", user.getLogin());
            throw new InvalidInputDataException("Неправильный пароль", 0);
        } catch (Exception e) {
            log.error("Ошибка при авторизации пользователя", e);
            throw e;
        }
    }

    /**
     * Деавторизация пользователя.
     *
     * @param authToken Токен аутентификации
     * @param request   Запрос
     * @param response  Ответ
     * @return Логин пользователя, вышедшего из системы
     */
    public String logout(String authToken, HttpServletRequest request, HttpServletResponse response) {
        try {
            log.debug("Получаем информацию об аутентификации");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = findUserInStorage(auth.getName());
            if (user != null) {
                log.info("Выполняем выход для пользователя {}", user.getLogin());
                new SecurityContextLogoutHandler().logout(request, response, auth);
                jwtProvider.addAuthTokenInBlackList(authToken);
                return user.getLogin();
            }
            log.warn("Не удалось выполнить выход");
            return null;
        } catch (Exception e) {
            log.error("Ошибка при выполнении выхода", e);
            return null;
        }
    }

    private User findUserInStorage(String login) {
        try {
            log.debug("Поиск пользователя по логину {}", login);
            return userRepository.findUserByLogin(login)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден по логину", 0));
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя по логину {}", login, e);
            throw e;
        }
    }

    private boolean isEquals(User userDTO, User userFromDatabase) {
        log.debug("Проверка пароля пользователя");
        return passwordEncoder.matches(userDTO.getPassword(), userFromDatabase.getPassword());
    }
}