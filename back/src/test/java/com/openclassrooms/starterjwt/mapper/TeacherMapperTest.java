package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

@ExtendWith(MockitoExtension.class)
public class TeacherMapperTest {
    
    private TeacherMapper teacherMapper;

    @BeforeEach
    void setUp() {
        teacherMapper = Mappers.getMapper(TeacherMapper.class);
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // Given
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        // When
        TeacherDto result = teacherMapper.toDto(teacher);

        // Then
        assertNotNull(result);
        assertEquals(teacher.getId(), result.getId());
        assertEquals(teacher.getFirstName(), result.getFirstName());
        assertEquals(teacher.getLastName(), result.getLastName());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Given
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setFirstName("John");
        dto.setLastName("Doe");

        // When
        Teacher result = teacherMapper.toEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getFirstName(), result.getFirstName());
        assertEquals(dto.getLastName(), result.getLastName());
    }
}