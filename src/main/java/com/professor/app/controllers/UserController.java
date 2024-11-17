package com.professor.app.controllers;

import com.professor.app.dto.users.UpdatePasswordDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.dto.users.UserUpdateDTO;
import com.professor.app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/id/{id}")
    public ResponseEntity<Optional<UserResponseDTO>> getUserById(@PathVariable String id) {
        Optional<UserResponseDTO> userResponseDTO = Optional.ofNullable(userService.findUserById(id));
        return  ResponseEntity.ok(userResponseDTO);
    }
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable String role) {
        List<UserResponseDTO>  userResponseDTOList = userService.findUsersByRole(role);
        return ResponseEntity.ok(userResponseDTOList);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAdminUser(@PathVariable String id,
                                                  @RequestBody UserUpdateDTO requestDTO) {
        String result = userService.updateUser(id, requestDTO);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{id}/update-password")
    public ResponseEntity<String> updatePassword(@PathVariable String id,
                                                 @RequestBody UpdatePasswordDTO request) {

           String updatePassword = userService.updatePassword(id, request.oldPassword(), request.newPassword());
           return ResponseEntity.ok(updatePassword);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        String deleteResult = userService.deleteUser(id);
        return ResponseEntity.ok(deleteResult);
    }
}
