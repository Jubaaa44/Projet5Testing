package com.openclassrooms.starterjwt.repository.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import javax.transaction.Transactional;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_teacher_repo;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true",
    "oc.app.jwtSecret=bezKoderSecretKey",
    "oc.app.jwtExpirationMs=86400000",
    "spring.main.allow-bean-definition-overriding=true",
    "spring.main.allow-circular-references=true"
})
@Transactional
public class TeacherRepositoryIntegrationTest {

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();
        
        teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher);
    }

    @Test
    void shouldSaveTeacherWithAllFields() {
        // Given
        Teacher newTeacher = new Teacher();
        newTeacher.setFirstName("Jane");
        newTeacher.setLastName("Smith");

        // When
        Teacher savedTeacher = teacherRepository.save(newTeacher);

        // Then
        assertNotNull(savedTeacher.getId());
        Teacher foundTeacher = teacherRepository.findById(savedTeacher.getId()).orElse(null);
        assertNotNull(foundTeacher);
        assertEquals("Jane", foundTeacher.getFirstName());
        assertEquals("Smith", foundTeacher.getLastName());
    }

    @Test
    void shouldFindAllTeachers() {
        // Given
        Teacher anotherTeacher = new Teacher();
        anotherTeacher.setFirstName("Jane");
        anotherTeacher.setLastName("Smith");
        teacherRepository.save(anotherTeacher);

        // When
        List<Teacher> teachers = teacherRepository.findAll();

        // Then
        assertEquals(2, teachers.size());
    }

    @Test
    void shouldDeleteTeacherWithoutDeletingSessions() {
        // Given
        Long teacherId = teacher.getId();

        // When
        teacherRepository.deleteById(teacherId);

        // Then
        assertFalse(teacherRepository.findById(teacherId).isPresent());
    }
}