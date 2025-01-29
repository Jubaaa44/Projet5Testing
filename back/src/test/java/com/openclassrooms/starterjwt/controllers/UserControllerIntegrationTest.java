package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

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
public class UserControllerIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private PasswordEncoder passwordEncoder;

   private User testUser;

   @BeforeEach
   void setUp() {
       userRepository.deleteAll();
       
       // Création d'un utilisateur de test
       testUser = new User();
       testUser.setEmail("test@test.com");
       testUser.setFirstName("John");
       testUser.setLastName("Doe");
       testUser.setPassword(passwordEncoder.encode("password123"));
       testUser.setAdmin(false);
       testUser = userRepository.save(testUser);
   }

   @Test
   @WithMockUser("test@test.com")
   void findById_ShouldReturnUser() throws Exception {
       mockMvc.perform(get("/api/user/{id}", testUser.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email", is("test@test.com")))
               .andExpect(jsonPath("$.firstName", is("John")))
               .andExpect(jsonPath("$.lastName", is("Doe")));
   }

   @Test
   @WithMockUser("test@test.com")
   void findById_ShouldReturn404_WhenUserNotFound() throws Exception {
       mockMvc.perform(get("/api/user/999"))
               .andExpect(status().isNotFound());
   }

   @Test
   void findById_ShouldReturn401_WhenNotAuthenticated() throws Exception {
       mockMvc.perform(get("/api/user/1"))
               .andExpect(status().isUnauthorized());
   }

   @Test
   @WithMockUser("test@test.com")
   void delete_ShouldDeleteUser_WhenUserDeletesTheirOwnAccount() throws Exception {
       mockMvc.perform(delete("/api/user/{id}", testUser.getId()))
               .andExpect(status().isOk());
   }

   @Test
   @WithMockUser("other@test.com")
   void delete_ShouldReturn401_WhenUserDeletesOtherAccount() throws Exception {
       mockMvc.perform(delete("/api/user/{id}", testUser.getId()))
               .andExpect(status().isUnauthorized());
   }

   @Test
   void delete_ShouldReturn401_WhenNotAuthenticated() throws Exception {
       mockMvc.perform(delete("/api/user/1"))
               .andExpect(status().isUnauthorized());
   }

   @Test
   @WithMockUser("test@test.com")
   void delete_ShouldReturn404_WhenUserNotFound() throws Exception {
       mockMvc.perform(delete("/api/user/999"))
               .andExpect(status().isNotFound());
   }
}