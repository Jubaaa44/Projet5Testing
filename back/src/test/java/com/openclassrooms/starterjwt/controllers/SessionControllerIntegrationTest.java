package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.dto.SessionDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "oc.app.jwtSecret=openclassroomssecret",
    "oc.app.jwtExpirationMs=86400000"
})
public class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        
        sessionDto = new SessionDto();
        sessionDto.setName("Test Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Test Description");
        sessionDto.setTeacher_id(1L);
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnAllSessions() throws Exception {
        // Given
        Session session = new Session();
        session.setName("Test Session");
        session.setDate(new Date());
        session.setDescription("Test Description");
        sessionRepository.save(session);

        // When & Then
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Test Session")));
    }

    @Test
    @WithMockUser
    void create_ShouldCreateNewSession() throws Exception {
        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sessionDto.getName())))
                .andExpect(jsonPath("$.description", is(sessionDto.getDescription())));
    }

    @Test
    @WithMockUser
    void getById_ShouldReturnSession() throws Exception {
        // Given
        Session session = new Session();
        session.setName("Test Session");
        session.setDate(new Date());
        session.setDescription("Test Description");
        Session savedSession = sessionRepository.save(session);

        // When & Then
        mockMvc.perform(get("/api/session/{id}", savedSession.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Session")));
    }

    @Test
    void getById_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isUnauthorized());
    }
}