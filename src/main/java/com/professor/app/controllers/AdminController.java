package com.professor.app.controllers;

import com.professor.app.dto.users.AdminRequestDTO;
import com.professor.app.dto.users.AdminUpdateRequestDTO;
import com.professor.app.dto.users.UserResponseDTO;
import com.professor.app.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin")
@AllArgsConstructor
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createAdminUser(@RequestBody AdminRequestDTO adminRequestDTO) {
        UserResponseDTO createdUser = adminService.saveAdminUser(adminRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAdminUser(@PathVariable String id,
                                                  @RequestBody AdminUpdateRequestDTO requestDTO) {
        String result = adminService.updateAdminUser(id, requestDTO);
        return ResponseEntity.ok(result);
    }
    @PatchMapping("/update/comments/{id}")
    public ResponseEntity<String> updateComments(@PathVariable String id,
                                                 @RequestParam String comments) {
        String result = adminService.updateComments(id, comments);
        return ResponseEntity.ok(result);
    }
}
