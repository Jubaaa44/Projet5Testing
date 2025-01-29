package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setAdmin(false);

        // When
        UserDto result = userMapper.toDto(user);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Given
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("test@test.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("password123"); // Ajout du password

        // When
        User result = userMapper.toEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getFirstName(), result.getFirstName());
        assertEquals(dto.getLastName(), result.getLastName());
        assertEquals(dto.getPassword(), result.getPassword()); // Vérification du password
    }
    @Test
    void toDtoList_ShouldMapAllUsers() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test1@test.com");
        user1.setFirstName("John");    // Ajout du firstName
        user1.setLastName("Doe");      // Ajout du lastName
        user1.setPassword("password1"); // Ajout du password
        user1.setAdmin(false);
        
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@test.com");
        user2.setFirstName("Jane");    // Ajout du firstName
        user2.setLastName("Smith");    // Ajout du lastName
        user2.setPassword("password2"); // Ajout du password
        user2.setAdmin(false);

        List<User> users = Arrays.asList(user1, user2);

        // When
        List<UserDto> result = userMapper.toDto(users);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user1.getId(), result.get(0).getId());
        assertEquals(user1.getEmail(), result.get(0).getEmail());
        assertEquals(user1.getFirstName(), result.get(0).getFirstName());
        assertEquals(user1.getLastName(), result.get(0).getLastName());
        
        assertEquals(user2.getId(), result.get(1).getId());
        assertEquals(user2.getEmail(), result.get(1).getEmail());
        assertEquals(user2.getFirstName(), result.get(1).getFirstName());
        assertEquals(user2.getLastName(), result.get(1).getLastName());
    }

    @Test
    void toEntityList_ShouldMapAllUserDtos() {
        // Given
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setEmail("test1@test.com");
        dto1.setFirstName("John");     // Ajout du firstName
        dto1.setLastName("Doe");       // Ajout du lastName
        dto1.setPassword("password1"); // Ajout du password
        
        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setEmail("test2@test.com");
        dto2.setFirstName("Jane");     // Ajout du firstName
        dto2.setLastName("Smith");     // Ajout du lastName
        dto2.setPassword("password2"); // Ajout du password

        List<UserDto> dtos = Arrays.asList(dto1, dto2);

        // When
        List<User> result = userMapper.toEntity(dtos);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1.getId(), result.get(0).getId());
        assertEquals(dto1.getEmail(), result.get(0).getEmail());
        assertEquals(dto1.getFirstName(), result.get(0).getFirstName());
        assertEquals(dto1.getLastName(), result.get(0).getLastName());
        
        assertEquals(dto2.getId(), result.get(1).getId());
        assertEquals(dto2.getEmail(), result.get(1).getEmail());
        assertEquals(dto2.getFirstName(), result.get(1).getFirstName());
        assertEquals(dto2.getLastName(), result.get(1).getLastName());
    }
}