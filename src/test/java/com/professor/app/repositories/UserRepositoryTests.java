package com.professor.app.repositories;

import com.professor.app.entities.User;
import com.professor.app.roles.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
public class UserRepositoryTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        mongoTemplate.dropCollection(User.class);

        user1 = new User();
        user1.setId("1");
        user1.setName("Alberto");
        user1.setSurname("Acosta");
        user1.setEmail("albertoacosta@casla.com");
        user1.setPassword("password1");
        user1.setDni("1000");
        user1.setRole(Role.ADMIN);
        user1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));
        user1.setModifiedAt(LocalDateTime.of(2024, 2, 2, 12, 0, 0));

        user2 = new User();
        user2.setId("2");
        user2.setName("Leandro");
        user2.setSurname("Romagnoli");
        user2.setEmail("leandroromagnoli@casla.com");
        user2.setPassword("password2");
        user2.setDni("2000");
        user2.setRole(Role.ADMIN);
        user2.setCreatedAt(LocalDateTime.of(2024, 1, 1, 13, 0, 0));
        user2.setModifiedAt(LocalDateTime.of(2024, 2, 2, 13, 0, 0));

        user3 = new User();
        user3.setId("3");
        user3.setName("Emilio");
        user3.setSurname("Acodado");
        user3.setEmail("emilioacodado@casla.com");
        user3.setPassword("password3");
        user3.setDni("3000");
        user3.setRole(Role.PROFESSOR);
        user3.setCreatedAt(LocalDateTime.of(2024, 1, 1, 13, 0, 0));
        user3.setModifiedAt(LocalDateTime.of(2024, 2, 2, 13, 0, 0));

        mongoTemplate.save(user1);
        mongoTemplate.save(user2);
        mongoTemplate.save(user3);
    }

    @Test
    @DisplayName("Find by email success test")
    public void findByEmailTestSuccess() {
        Optional<User> result = userRepository.findByEmail("albertoacosta@casla.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("albertoacosta@casla.com");
        assertThat(result.get().getName()).isEqualTo("Alberto");
    }

    @Test
    @DisplayName("Find by email not found test")
    public void findByEmailTestNotFound() {
        Optional<User> result = userRepository.findByEmail("noexistente@casla.com");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Find users by role")
    public void findUsersByRole() {
        List<User> result = userRepository.findUsersByRole(Role.ADMIN.name());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting("email")
                .containsExactlyInAnyOrder("albertoacosta@casla.com", "leandroromagnoli@casla.com");
    }
    @Test
    void testFindByNameIgnoreCaseOrSurnameIgnoreCase() {

        List<User> result = userRepository.findByNameContainingOrSurnameContainingIgnoreCase("ac");

        System.out.println(result);
        assertEquals(2, result.size());
    }


}
