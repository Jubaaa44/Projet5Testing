/*package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    "oc.app.jwtSecret=bezKoderSecretKey",
    "oc.app.jwtExpirationMs=86400000",
    "spring.main.allow-bean-definition-overriding=true",
    "spring.main.allow-circular-references=true"
})
class TeacherServiceIntegrationTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        // Nettoyage
        teacherRepository.deleteAll();

        // Création d'un teacher
        teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);
    }

    @Test
    void findAll_ShouldReturnAllTeachers() {
        // Given
        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");
        teacherRepository.save(teacher2);

        // When
        List<Teacher> result = teacherService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getFirstName().equals("John")));
        assertTrue(result.stream().anyMatch(t -> t.getFirstName().equals("Jane")));
    }

    @Test
    void findById_ShouldReturnTeacher_WhenTeacherExists() {
        // When
        Teacher result = teacherService.findById(teacher.getId());

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void findById_ShouldReturnNull_WhenTeacherDoesNotExist() {
        // When
        Teacher result = teacherService.findById(999L);

        // Then
        assertNull(result);
    }
}
*/