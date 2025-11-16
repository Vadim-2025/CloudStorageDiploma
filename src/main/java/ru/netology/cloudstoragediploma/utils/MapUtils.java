package ru.netology.cloudstoragediploma.utils;

import org.springframework.stereotype.Service;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.entity.User;

@Service
public class MapUtils {
    //Преобразование из DTO в Entity
    public User toUserEntity(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    //Преобразование из Entity в DTO
    public UserDTO toUserDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(user.getLogin());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }
}