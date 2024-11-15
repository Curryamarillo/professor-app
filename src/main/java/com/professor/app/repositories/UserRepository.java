package com.professor.app.repositories;

import com.professor.app.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // All users queries

    Optional<User> findByEmail(String email);

    List<User> findUsersByRole(String role);

    List<User> findByRoleAndNameIgnoreCaseOrSurnameIgnoreCase(String role, String searchTerm);

    List<User> findByNameIgnoreCaseOrSurnameIgnoreCase(String searchTerm);

    // Admin queries

    // Assistant queries

    // Professor queries

    // Tutor queries

    // Student queries
}
