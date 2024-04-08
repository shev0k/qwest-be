package com.qwest.backend.controller;

import com.qwest.backend.DTO.AuthorDTO;
import com.qwest.backend.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @Test
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
    void createAuthorTest() throws Exception {
        AuthorDTO newAuthor = new AuthorDTO();
        newAuthor.setFirstName("Jane");
        newAuthor.setLastName("Doe");

        AuthorDTO savedAuthor = new AuthorDTO();
        savedAuthor.setId(1L);
        savedAuthor.setFirstName("Jane");
        savedAuthor.setLastName("Doe");

        when(authorService.save(any(AuthorDTO.class))).thenReturn(savedAuthor);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    void updateAuthorTest() throws Exception {
        Long authorId = 1L;
        AuthorDTO existingAuthor = new AuthorDTO();
        existingAuthor.setId(authorId);
        existingAuthor.setFirstName("John");
        existingAuthor.setLastName("Doe");

        AuthorDTO updatedAuthor = new AuthorDTO();
        updatedAuthor.setId(authorId);
        updatedAuthor.setFirstName("UpdatedJohn");
        updatedAuthor.setLastName("Doe");

        when(authorService.findById(authorId)).thenReturn(Optional.of(existingAuthor));

        when(authorService.save(any(AuthorDTO.class))).thenReturn(updatedAuthor);

        mockMvc.perform(put("/api/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"UpdatedJohn\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("UpdatedJohn")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    void updateAuthor_NotFound() throws Exception {
        Long nonExistentAuthorId = 99L;

        when(authorService.findById(nonExistentAuthorId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/authors/{id}", nonExistentAuthorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Nonexistent\",\"lastName\":\"Author\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAuthor_Success() throws Exception {
        Long authorId = 1L;

        when(authorService.findById(authorId)).thenReturn(Optional.of(new AuthorDTO()));

        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isNoContent());

        verify(authorService).deleteById(authorId);
    }

    @Test
    void deleteAuthor_NotFound() throws Exception {
        Long nonExistentAuthorId = 99L;

        when(authorService.findById(nonExistentAuthorId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/authors/{id}", nonExistentAuthorId))
                .andExpect(status().isNotFound());
    }



    @Test
    void deleteAuthorTest() throws Exception {
        Long authorId = 1L;
        when(authorService.findById(authorId)).thenReturn(Optional.of(new AuthorDTO()));

        doNothing().when(authorService).deleteById(authorId);

        mockMvc.perform(delete("/api/authors/{id}", authorId))
                .andExpect(status().isNoContent());

        verify(authorService).deleteById(authorId);
    }
}
