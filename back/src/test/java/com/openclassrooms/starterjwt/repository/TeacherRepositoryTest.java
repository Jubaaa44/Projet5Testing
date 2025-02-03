package com.openclassrooms.starterjwt.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.openclassrooms.starterjwt.models.Teacher;

@DataJpaTest
public class TeacherRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void save_ShouldPersistTeacher() {
        // Given
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        // When
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Then
        assertNotNull(savedTeacher.getId());
        assertEquals("John", savedTeacher.getFirstName());
        assertEquals("Doe", savedTeacher.getLastName());
    }

    @Test
    void findById_ShouldReturnTeacher_WhenExists() {
        // Given
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        entityManager.persist(teacher);
        entityManager.flush();

        // When
        Optional<Teacher> found = teacherRepository.findById(teacher.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("Doe", found.get().getLastName());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDoesNotExist() {
        // When
        Optional<Teacher> found = teacherRepository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_ShouldRemoveTeacher() {
        // Given
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        entityManager.persist(teacher);
        entityManager.flush();
        Long id = teacher.getId();

        // When
        teacherRepository.deleteById(id);

        // Then
        Optional<Teacher> found = teacherRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllTeachers() {
        // Given
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");
        entityManager.persist(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");
        entityManager.persist(teacher2);
        entityManager.flush();

        // When
        List<Teacher> teachers = teacherRepository.findAll();

        // Then
        assertEquals(2, teachers.size());
    }
}