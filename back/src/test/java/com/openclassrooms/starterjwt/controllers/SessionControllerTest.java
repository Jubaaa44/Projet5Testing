package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);

        session = new Session();
        session.setId(1L);
        session.setName("Yoga Session");
        session.setDate(new Date());
        session.setDescription("Description test");
        session.setTeacher(teacher);

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Description test");
        sessionDto.setTeacher_id(1L);
    }

    @Test
    void findById_ShouldReturnSession() {
        // Given
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // When
        ResponseEntity<?> response = sessionController.findById("1");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).getById(1L);
        verify(sessionMapper).toDto(session);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenSessionDoesNotExist() {
        // Given
        when(sessionService.getById(1L)).thenReturn(null);

        // When
        ResponseEntity<?> response = sessionController.findById("1");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findById_ShouldReturnBadRequest_WhenIdIsNotNumeric() {
        // When
        ResponseEntity<?> response = sessionController.findById("invalid");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findAll_ShouldReturnAllSessions() {
        // Given
        List<Session> sessions = Arrays.asList(session);
        List<SessionDto> sessionDtos = Arrays.asList(sessionDto);
        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        // When
        ResponseEntity<?> response = sessionController.findAll();

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).findAll();
        verify(sessionMapper).toDto(sessions);
    }

    @Test
    void create_ShouldReturnCreatedSession() {
        // Given
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.create(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // When
        ResponseEntity<?> response = sessionController.create(sessionDto);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionMapper).toEntity(sessionDto);
        verify(sessionService).create(session);
        verify(sessionMapper).toDto(session);
    }

    @Test
    void update_ShouldReturnUpdatedSession() {
        // Given
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.update(eq(1L), any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // When
        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionMapper).toEntity(sessionDto);
        verify(sessionService).update(eq(1L), any(Session.class));
        verify(sessionMapper).toDto(session);
    }

    @Test
    void delete_ShouldReturnOk_WhenSessionExists() {
        // Given
        when(sessionService.getById(1L)).thenReturn(session);

        // When
        ResponseEntity<?> response = sessionController.save("1");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).delete(1L);
    }

    @Test
    void participate_ShouldReturnOk() {
        // When
        ResponseEntity<?> response = sessionController.participate("1", "1");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).participate(1L, 1L);
    }

    @Test
    void noLongerParticipate_ShouldReturnOk() {
        // When
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "1");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(sessionService).noLongerParticipate(1L, 1L);
    }
}