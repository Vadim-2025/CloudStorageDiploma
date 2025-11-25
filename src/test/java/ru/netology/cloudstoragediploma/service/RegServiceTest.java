package ru.netology.cloudstoragediploma.service;

import org.junit.jupiter.api.Assertions;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.entity.User;
import ru.netology.cloudstoragediploma.enums.Role;
import ru.netology.cloudstoragediploma.exception.UserAlreadyExistsException;
import ru.netology.cloudstoragediploma.exception.UserNotFoundException;
import ru.netology.cloudstoragediploma.repository.UserRepository;
import ru.netology.cloudstoragediploma.utils.MapUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RegServiceTest {

    @Autowired
    private RegService regService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private MapUtils mapUtils;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void init() {
        userDTO = UserDTO.builder()
                .login("testLogin@test.ru")
                .password("testPassword")
                .build();

        user = User.builder()
                .id(1L)
                .login("testLogin@test.ru")
                .password("encodedPassword")
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();
    }

    @Test
    public void testRegisterUser() {
        // Готовим моки
        Mockito.when(userRepository.findUserByLogin(userDTO.getLogin())).thenReturn(Optional.empty());
        Mockito.when(mapUtils.toUserEntity(userDTO)).thenReturn(user);
        Mockito.when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");

        // Действие
        User registeredUser = regService.regUser(user);

        // Проверка результата
        Assertions.assertNotNull(registeredUser);
        Assertions.assertEquals(userDTO.getLogin(), registeredUser.getLogin());
        Assertions.assertEquals("encodedPassword", registeredUser.getPassword());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void testRegisterUser_ExistingUser_ExceptionThrown() {
        // Готовим моки
        Mockito.when(userRepository.findUserByLogin(userDTO.getLogin())).thenReturn(Optional.of(user));

        // Проверяем, что исключение выброшено
        Assertions.assertThrows(UserAlreadyExistsException.class, () -> regService.regUser(user));
    }

    @Test
    public void testGetUser_UserFound_Success() {
        // Подготовили моки
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Действие
        User retrievedUser = regService.getUser(user.getId());

        // Проверка результата
        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals(user.getLogin(), retrievedUser.getLogin());
    }

    @Test
    public void testGetUser_UserNotFound_ExceptionThrown() {
        // Подготовили моки
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Проверяем, что исключение выброшено
        Assertions.assertThrows(UserNotFoundException.class, () -> regService.getUser(user.getId()));
    }

    @Test
    public void testDeleteUser_UserFound_Success() {
        // Подготовили моки
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Действие
        regService.deleteUser(user.getId());

        // Проверка, что произошло удаление
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(user.getId());
    }

    @Test
    public void testDeleteUser_UserNotFound_ExceptionThrown() {
        // Подготовили моки
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Проверяем, что исключение выброшено
        Assertions.assertThrows(UserNotFoundException.class, () -> regService.deleteUser(user.getId()));
    }
}