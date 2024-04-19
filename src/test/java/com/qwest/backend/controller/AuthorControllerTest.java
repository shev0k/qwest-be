package com.qwest.backend.controller;

import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.configuration.security.SecurityConfig;
import com.qwest.backend.business.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@Import({SecurityConfig.class, JwtUtil.class})

class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void getAllAuthorsTest() throws Exception {
        AuthorDTO author1 = new AuthorDTO();
        author1.setId(1L);
        author1.setFirstName("John");
        author1.setLastName("Doe");

        when(authorService.findAll()).thenReturn(List.of(author1));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void getAuthorByIdTest() throws Exception {
        AuthorDTO author = new AuthorDTO();
        author.setId(1L);
        author.setFirstName("John");

        when(authorService.findById(1L)).thenReturn(Optional.of(author));

        mockMvc.perform(get("/api/authors/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void createAuthorTest() throws Exception {
        AuthorDTO newAuthor = new AuthorDTO();
        newAuthor.setFirstName("Jane");
        newAuthor.setLastName("Doe");
        newAuthor.setEmail("jane.doe@example.com");

        AuthorDTO savedAuthor = new AuthorDTO();
        savedAuthor.setId(1L);
        savedAuthor.setFirstName("Jane");
        savedAuthor.setLastName("Doe");
        savedAuthor.setEmail("jane.doe@example.com");

        when(authorService.save(any(AuthorDTO.class))).thenReturn(savedAuthor);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\", \"email\":\"jane.doe@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("jane.doe@example.com")));
    }


    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void updateAuthorTest() throws Exception {
        Long authorId = 1L;
        AuthorDTO existingAuthor = new AuthorDTO();
        existingAuthor.setId(authorId);
        existingAuthor.setFirstName("John");
        existingAuthor.setLastName("Doe");
        existingAuthor.setEmail("john.doe@example.com"); // Ensure the existing author has an email

        AuthorDTO updatedAuthor = new AuthorDTO();
        updatedAuthor.setId(authorId);
        updatedAuthor.setFirstName("UpdatedJohn");
        updatedAuthor.setLastName("Doe");
        updatedAuthor.setEmail("updated.john.doe@example.com"); // Include the email in the updated data

        when(authorService.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorService.update(eq(authorId), any(AuthorDTO.class))).thenReturn(updatedAuthor);

        mockMvc.perform(put("/api/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"UpdatedJohn\",\"lastName\":\"Doe\", \"email\":\"updated.john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("UpdatedJohn")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("updated.john.doe@example.com")));
    }


    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void updateAuthor_NotFound() throws Exception {
        Long nonExistentAuthorId = 99L;

        when(authorService.findById(nonExistentAuthorId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/authors/{id}", nonExistentAuthorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Nonexistent\",\"lastName\":\"Author\", \"email\":\"nonexistent.author@example.com\"}"))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteAuthor_Success() throws Exception {
        Long authorId = 1L;

        when(authorService.findById(authorId)).thenReturn(Optional.of(new AuthorDTO()));

        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isNoContent());

        verify(authorService).deleteById(authorId);
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void deleteAuthor_NotFound() throws Exception {
        Long nonExistentAuthorId = 99L;

        when(authorService.findById(nonExistentAuthorId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/authors/{id}", nonExistentAuthorId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void login_Successful_ReturnsJwt() throws Exception {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("user@example.com");
        authorDTO.setPassword("correctpassword");
        authorDTO.setJwt("mock-jwt-token");

        when(authorService.login(any(AuthorDTO.class))).thenReturn(Optional.of(authorDTO));

        // Perform the login attempt
        mockMvc.perform(post("/api/authors/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"correctpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer mock-jwt-token"))
                .andExpect(jsonPath("$.email", is("user@example.com")))
                .andExpect(jsonPath("$.jwt", is("mock-jwt-token")));
    }

    @Test
    @WithMockUser(username="admin", roles={"FOUNDER"})
    void login_Unsuccessful_ReturnsUnauthorized() throws Exception {
        when(authorService.login(any(AuthorDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/authors/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

}
