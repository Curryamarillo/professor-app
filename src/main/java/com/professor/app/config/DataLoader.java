package com.professor.app.config;

import com.professor.app.dto.AdminRequestDTO;
import com.professor.app.entities.Admin;
import com.professor.app.roles.Role;
import com.professor.app.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.professor.app.roles.Role.ADMIN;

@Configuration
public class DataLoader {
    @Bean
    public CommandLineRunner loadData(UserService userService) {
        return args -> {
            // Creates an Admin Object
            AdminRequestDTO admin = new AdminRequestDTO("John", "Doe", "johndoe@tests.com",  "00001", ADMIN, "Comments from a test user");
            userService.saveUser(admin);
        };
    }
}
