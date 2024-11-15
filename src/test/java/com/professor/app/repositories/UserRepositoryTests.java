package com.professor.app.repositories;

import com.professor.app.entities.User;
import com.professor.app.roles.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataMongoTest
public class UserRepositoryTests {


    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    private UserRepository userRepository;

    User user1;
    User user2;
    User user3;


    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setId("1");
        user1.setName("Alberto");
        user1.setSurname("Acosta");
        user1.setEmail("albertoacosta@casla.com");
        user1.setPassword("password1");
        user1.setDni("1000");
        user1.setRole(Role.ADMIN);
        user1.setCreatedAt(LocalDateTime.of(2024, 01, 01, 12, 00, 00));
        user1.setModifiedAt(LocalDateTime.of(2024, 02, 02, 12, 00, 00));

        user2 = new User();
        user2.setId("2");
        user2.setName("Leandro");
        user2.setSurname("Romagnoli");
        user2.setEmail("leandroromagnoli@casla.com");
        user2.setPassword("password2");
        user2.setDni("2000");
        user2.setRole(Role.ADMIN);
        user2.setCreatedAt(LocalDateTime.of(2024, 01, 01, 13, 00, 00));
        user2.setModifiedAt(LocalDateTime.of(2024, 02, 02, 13, 00, 00));

        user3 = new User();
        user3.setId("3");
        user3.setName("Emilio");
        user3.setSurname("Acodado");
        user3.setEmail("emilioacodado@casla.com");
        user3.setPassword("password3");
        user3.setDni("3000");
        user3.setRole(Role.PROFESSOR);
        user3.setCreatedAt(LocalDateTime.of(2024, 01, 01, 13, 00, 00));
        user3.setModifiedAt(LocalDateTime.of(2024, 02, 02, 13, 00, 00));

    }

    @Test
    @DisplayName("Find by email success test")
    public void findByEmailTestSuccess() {
        // given

        Mockito.when(userRepository.findByEmail("albertoacosta@casla.com")).thenReturn(Optional.of(user1));
        // when
        Optional<User> result = userRepository.findByEmail("albertoacosta@casla.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("albertoacosta@casla.com");
        assertThat(result.get().getName()).isEqualTo("Alberto");
        assertThat(result.get().getSurname()).isEqualTo("Acosta");
        assertThat(result.get().getPassword()).isEqualTo("password1");
        assertThat(result.get().getDni()).isEqualTo("1000");
        assertThat(result.get().getRole()).isEqualTo(Role.ADMIN);
}

    @Test
    @DisplayName("Find by email not found test")
    public void findByEmailTestNotFound() {
        // given
        String emailNotFound = "noexistente@casla.com";

        // when
        Optional<User> result = userRepository.findByEmail(emailNotFound);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Find users by role")
    public void findUsersByRole() {
        // given
        String role = "ADMIN";
        Mockito.when(userRepository.findUsersByRole(role)).thenReturn(List.of(user1, user2));

        // when
        List<User> result = userRepository.findUsersByRole(role);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.get(1).getRole()).isEqualTo(Role.ADMIN);

        assertThat(result.get(0).getEmail()).isEqualTo("albertoacosta@casla.com");
        assertThat(result.get(0).getName()).isEqualTo("Alberto");
        assertThat(result.get(0).getSurname()).isEqualTo("Acosta");

        assertThat(result.get(1).getEmail()).isEqualTo("leandroromagnoli@casla.com");
        assertThat(result.get(1).getName()).isEqualTo("Leandro");
        assertThat(result.get(1).getSurname()).isEqualTo("Romagnoli");

        assertThat(result.get(0).getDni()).isEqualTo("1000");
        assertThat(result.get(1).getDni()).isEqualTo("2000");

        assertThat(result.get(0).getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 01, 01, 12, 00, 00));
        assertThat(result.get(0).getModifiedAt()).isEqualTo(LocalDateTime.of(2024, 02, 02, 12, 00, 00));

        assertThat(result.get(1).getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 01, 01, 13, 00, 00));
        assertThat(result.get(1).getModifiedAt()).isEqualTo(LocalDateTime.of(2024, 02, 02, 13, 00, 00));

    }

    @Test
    @DisplayName("Find by role, and name or surname ignoring case")
    public void findByRoleAndNameIgnoreCaseOrSurnameTest() {
        String role = "ADMIN";
        String searchTerm = "ac";

        Mockito.when(userRepository.findByRoleAndNameIgnoreCaseOrSurnameIgnoreCase(role, searchTerm))
                .thenReturn(List.of(user1, user2));

        List<User> result = userRepository.findByRoleAndNameIgnoreCaseOrSurnameIgnoreCase(role, searchTerm);


        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.get(0).getName()).isEqualTo("Alberto");
        assertThat(result.get(0).getSurname()).isEqualTo("Acosta");
        assertThat(result.get(0).getEmail()).isEqualTo("albertoacosta@casla.com");
        assertThat(result.get(0).getDni()).isEqualTo("1000");
        assertThat(result.get(0).getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 01, 01, 12, 00, 00));
        assertThat(result.get(0).getModifiedAt()).isEqualTo(LocalDateTime.of(2024, 02, 02, 12, 00, 00));

        assertThat(result.get(1).getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.get(1).getName()).isEqualTo("Leandro");
        assertThat(result.get(1).getSurname()).isEqualTo("Romagnoli");
        assertThat(result.get(1).getEmail()).isEqualTo("leandroromagnoli@casla.com");
        assertThat(result.get(1).getDni()).isEqualTo("2000");
        assertThat(result.get(1).getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 01, 01, 13, 00, 00));
        assertThat(result.get(1).getModifiedAt()).isEqualTo(LocalDateTime.of(2024, 02, 02, 13, 00, 00));
    }


    @Test
    @DisplayName("Find by name or surname ignoring case")
    public void findByNameIgnoreCaseOrSurnameIgnoreCase() {
        String searchTerm = "ac";

        Mockito.when(userRepository.findByNameIgnoreCaseOrSurnameIgnoreCase(searchTerm)).thenReturn(List.of(user1, user3));

        List<User> result = userRepository.findByNameIgnoreCaseOrSurnameIgnoreCase(searchTerm);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.get(0).getName()).isEqualTo("Alberto");
        assertThat(result.get(0).getSurname()).isEqualTo("Acosta");
        assertThat(result.get(0).getEmail()).isEqualTo("albertoacosta@casla.com");
        assertThat(result.get(0).getDni()).isEqualTo("1000");
        assertThat(result.get(0).getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 01, 01, 12, 00, 00));
        assertThat(result.get(0).getModifiedAt()).isEqualTo(LocalDateTime.of(2024, 02, 02, 12, 00, 00));

        assertThat(result.get(1).getRole()).isEqualTo(Role.PROFESSOR);
        assertThat(result.get(1).getName()).isEqualTo("Emilio");
        assertThat(result.get(1).getSurname()).isEqualTo("Acodado");
        assertThat(result.get(1).getEmail()).isEqualTo("emilioacodado@casla.com");
        assertThat(result.get(1).getDni()).isEqualTo("3000");
        assertThat(result.get(1).getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 01, 01, 13, 00, 00));
        assertThat(result.get(1).getModifiedAt()).isEqualTo(LocalDateTime.of(2024, 02, 02, 13, 00, 00));

    }



}
