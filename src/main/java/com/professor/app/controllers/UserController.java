package com.professor.app.controllers;

import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> userResponseDTOList = userService.findAllUsers();
        return ResponseEntity.ok(userResponseDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<UserResponseDTO>> getUserById(@PathVariable String id) {
        Optional<UserResponseDTO> userResponseDTO = userService.findUserById(id);
        return  ResponseEntity.ok(userResponseDTO);
    }
    @GetMapping("/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        List<UserResponseDTO>  userResponseDTOList = userService.findUsersByRole(role);
        return ResponseEntity.ok(userResponseDTOList);
    }
}
