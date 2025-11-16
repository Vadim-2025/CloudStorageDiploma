package ru.netology.cloudstoragediploma.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstoragediploma.dto.UserDTO;
import ru.netology.cloudstoragediploma.service.RegService;

import jakarta.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class RegController {
    private final RegService regService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> regUser(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(regService.regUser(userDTO), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(regService.getUser(id), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        regService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}