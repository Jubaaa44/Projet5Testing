package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {
    
    @Mock
    private TeacherRepository teacherRepository;
    
    @InjectMocks
    private TeacherService teacherService;
    
    private Teacher teacher;
    
    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
    }
    
    @Test
    void findAll_ShouldReturnListOfTeachers() {
        // Given
        List<Teacher> expectedTeachers = Arrays.asList(teacher);
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);
        
        // When
        List<Teacher> actualTeachers = teacherService.findAll();
        
        // Then
        assertNotNull(actualTeachers);
        assertEquals(expectedTeachers.size(), actualTeachers.size());
        assertEquals(expectedTeachers.get(0).getId(), actualTeachers.get(0).getId());
        verify(teacherRepository).findAll();
    }
    
    @Test
    void findById_ShouldReturnTeacher_WhenTeacherExists() {
        // Given
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        
        // When
        Teacher result = teacherService.findById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(teacher.getId(), result.getId());
        assertEquals(teacher.getFirstName(), result.getFirstName());
        assertEquals(teacher.getLastName(), result.getLastName());
        verify(teacherRepository).findById(1L);
    }
    
    @Test
    void findById_ShouldReturnNull_WhenTeacherDoesNotExist() {
        // Given
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Teacher result = teacherService.findById(1L);
        
        // Then
        assertNull(result);
        verify(teacherRepository).findById(1L);
    }
}