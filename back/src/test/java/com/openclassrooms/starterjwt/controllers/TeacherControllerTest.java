package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        // Initialize Teacher
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Doe");
        teacher.setFirstName("John");

        // Initialize TeacherDto
        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("Doe");
        teacherDto.setFirstName("John");
    }

    @Test
    void findById_ShouldReturnTeacher() {
        // Given
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // When
        ResponseEntity<?> response = teacherController.findById("1");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(teacherService).findById(1L);
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    void findById_ShouldReturnNotFound_WhenTeacherDoesNotExist() {
        // Given
        when(teacherService.findById(1L)).thenReturn(null);

        // When
        ResponseEntity<?> response = teacherController.findById("1");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findById_ShouldReturnBadRequest_WhenIdIsNotNumeric() {
        // When
        ResponseEntity<?> response = teacherController.findById("invalid");

        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void findAll_ShouldReturnAllTeachers() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto);
        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        // When
        ResponseEntity<?> response = teacherController.findAll();

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(teacherService).findAll();
        verify(teacherMapper).toDto(teachers);
    }
}