package ru.netology.cloudstoragediploma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.model.Token;
import ru.netology.cloudstoragediploma.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Token login(@RequestBody UserDTO userDTO) {
        return authService.login(userDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String authToken,
                                    HttpServletRequest request, HttpServletResponse response)
    {
        if (authService.logout(authToken, request, response) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(HttpStatus.OK);
        }
    }
}