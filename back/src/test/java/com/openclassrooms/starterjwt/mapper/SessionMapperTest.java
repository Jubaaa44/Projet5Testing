package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.mapstruct.factory.Mappers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class SessionMapperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    private SessionMapper sessionMapper;
    private Session session;
    private SessionDto sessionDto;
    private Teacher teacher;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialisation du mapper
        sessionMapper = Mappers.getMapper(SessionMapper.class);
        ReflectionTestUtils.setField(sessionMapper, "teacherService", teacherService);
        ReflectionTestUtils.setField(sessionMapper, "userService", userService);

        // Initialisation du teacher
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        // Initialisation du user
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        // Initialisation de la session
        session = new Session();
        session.setId(1L);
        session.setName("Yoga Session");
        session.setDate(new Date());
        session.setDescription("Test Description");
        session.setTeacher(teacher);
        session.setUsers(Arrays.asList(user));

        // Initialisation du sessionDto
        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Test Description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L));
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Given
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user);

        // When
        Session result = sessionMapper.toEntity(sessionDto);

        // Then
        assertNotNull(result);
        assertEquals(sessionDto.getId(), result.getId());
        assertEquals(sessionDto.getName(), result.getName());
        assertEquals(sessionDto.getDescription(), result.getDescription());
        assertEquals(sessionDto.getDate(), result.getDate());
        assertEquals(teacher, result.getTeacher());
        assertEquals(1, result.getUsers().size());
        assertEquals(user, result.getUsers().get(0));
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // When
        SessionDto result = sessionMapper.toDto(session);

        // Then
        assertNotNull(result);
        assertEquals(session.getId(), result.getId());
        assertEquals(session.getName(), result.getName());
        assertEquals(session.getDescription(), result.getDescription());
        assertEquals(session.getDate(), result.getDate());
        assertEquals(session.getTeacher().getId(), result.getTeacher_id());
        assertEquals(1, result.getUsers().size());
        assertEquals(user.getId(), result.getUsers().get(0));
    }

    @Test
    void toDto_ShouldHandleNullValues() {
        // Given
        session.setTeacher(null);
        session.setUsers(null);

        // When
        SessionDto result = sessionMapper.toDto(session);

        // Then
        assertNotNull(result);
        assertNull(result.getTeacher_id());
        assertTrue(result.getUsers().isEmpty());
    }

    @Test
    void toEntity_ShouldHandleNullValues() {
        // Given
        sessionDto.setTeacher_id(null);
        sessionDto.setUsers(null);

        // When
        Session result = sessionMapper.toEntity(sessionDto);

        // Then
        assertNotNull(result);
        assertNull(result.getTeacher());
        assertTrue(result.getUsers().isEmpty());
    }
}