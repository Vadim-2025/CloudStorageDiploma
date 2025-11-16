package ru.netology.cloudstoragediploma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.enums.Role;
import ru.netology.cloudstoragediploma.exception.UserAlreadyExistsException;
import ru.netology.cloudstoragediploma.exception.UserNotFoundException;
import ru.netology.cloudstoragediploma.repository.UserRepository;
import ru.netology.cloudstoragediploma.utils.MapUtils;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegService {
    private final UserRepository userRepository;
    private final MapUtils mapUtils;
    private final PasswordEncoder passwordEncoder;

    //Проверка наличия пользователя в базе данных. Если есть, пробрасываем исключение, если нет, регистрируем:
    public UserDTO regUser(UserDTO userDTO) {
        User user = mapUtils.toUserEntity(userDTO);
        userRepository.findUserByLogin(user.getLogin()).ifPresent(s -> {
            throw new UserAlreadyExistsException("User already exists", user.getId());
        });
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setRole(Role.ROLE_USER.getAuthority());
        return mapUtils.toUserDto(userRepository.save(user));
    }

    //Поиск пользователя по ID, если нет, пробрасываем исключение:
    public UserDTO getUser(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found", id));
        return mapUtils.toUserDto(foundUser);
    }

    //Удаление пользователя по ID. Если нет, пробрасываем исключение:
    public void deleteUser(Long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found", id));
        userRepository.deleteById(id);
    }
}