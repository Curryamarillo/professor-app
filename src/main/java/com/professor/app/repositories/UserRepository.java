package com.professor.app.repositories;

import com.professor.app.dto.UserResponseDTO;
import com.professor.app.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<UserResponseDTO> findByEmail(String email);
}
