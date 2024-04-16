package com.qwest.backend.service;

import com.qwest.backend.DTO.AuthorDTO;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.mapper.AuthorMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private AuthorDTO authorDTO;
    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setEmail("john.doe@example.com");

        authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");
        authorDTO.setEmail("john.doe@example.com");
    }

    @Test
    void findAllTest() {
        when(authorRepository.findAll()).thenReturn(Collections.singletonList(author));
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);

        List<AuthorDTO> results = authorService.findAll();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(authorDTO.getFirstName(), results.get(0).getFirstName());
    }

    @Test
    void findByIdTest() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);

        Optional<AuthorDTO> result = authorService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(authorDTO.getFirstName(), result.get().getFirstName());
    }

    @Test
    void saveAuthorTest() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);
        when(authorMapper.toEntity(any(AuthorDTO.class))).thenReturn(author);
        when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");

        authorDTO.setPassword("password");
        AuthorDTO savedAuthor = authorService.save(authorDTO);

        assertNotNull(savedAuthor);
        assertEquals(authorDTO.getFirstName(), savedAuthor.getFirstName());
        verify(passwordEncoder).encode("password");
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void deleteByIdTest() {
        doNothing().when(authorRepository).deleteById(1L);

        authorService.deleteById(1L);

        verify(authorRepository, times(1)).deleteById(1L);
    }

    @Test
    void createNewAuthorWithoutIdAssignsTravelerRole() {
        AuthorDTO newAuthorDTO = new AuthorDTO();
        newAuthorDTO.setFirstName("New");
        newAuthorDTO.setLastName("Author");
        newAuthorDTO.setPassword("newPassword");

        Author newAuthor = new Author();
        when(authorMapper.toEntity(any(AuthorDTO.class))).thenReturn(newAuthor);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedPassword");
        when(authorRepository.save(any(Author.class))).then(invocation -> {
            Author savedAuthor = invocation.getArgument(0);
            savedAuthor.setId(2L); // Simulate setting ID on save
            return savedAuthor;
        });
        when(authorMapper.toDto(any(Author.class))).thenReturn(newAuthorDTO);

        AuthorDTO result = authorService.save(newAuthorDTO);

        assertNotNull(result);
        verify(authorRepository).save(newAuthor);
        assertEquals(AuthorRole.TRAVELER, newAuthor.getRole());
    }

    @Test
    void updateAuthorWithPasswordHashing() {
        authorDTO.setPassword("newPassword");

        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(authorMapper.toEntity(any(AuthorDTO.class))).thenReturn(author);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedPassword");
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        AuthorDTO updatedAuthor = authorService.save(authorDTO);

        assertNotNull(updatedAuthor);
        assertEquals("hashedPassword", author.getPasswordHash());
        verify(passwordEncoder).encode("newPassword");
        verify(authorRepository).save(author);
    }

    @Test
    void createAuthorWithEmptyPassword() {
        AuthorDTO emptyPasswordAuthorDTO = new AuthorDTO();
        emptyPasswordAuthorDTO.setFirstName("Empty");
        emptyPasswordAuthorDTO.setLastName("Password");
        emptyPasswordAuthorDTO.setPassword("");

        Author emptyPasswordAuthor = new Author();
        when(authorMapper.toEntity(any(AuthorDTO.class))).thenReturn(emptyPasswordAuthor);
        when(authorRepository.save(any(Author.class))).thenReturn(emptyPasswordAuthor);
        when(authorMapper.toDto(any(Author.class))).thenReturn(emptyPasswordAuthorDTO);

        AuthorDTO result = authorService.save(emptyPasswordAuthorDTO);

        assertNotNull(result);
        assertNull(emptyPasswordAuthor.getPasswordHash());
        verify(passwordEncoder, never()).encode(anyString());
        verify(authorRepository).save(emptyPasswordAuthor);
    }

    @Test
    void saveAuthorWithNullPassword() {
        AuthorDTO authorWithNullPassword = new AuthorDTO();
        authorWithNullPassword.setId(1L);
        authorWithNullPassword.setFirstName("Jane");
        authorWithNullPassword.setLastName("Doe");
        authorWithNullPassword.setPassword(null);

        Author authorEntity = new Author();
        when(authorMapper.toEntity(any(AuthorDTO.class))).thenReturn(authorEntity);
        when(authorRepository.save(any(Author.class))).thenReturn(authorEntity);
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorWithNullPassword);

        AuthorDTO result = authorService.save(authorWithNullPassword);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertNull(result.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(authorRepository).save(authorEntity);
    }

    @Test
    void saveAuthorWithEmptyPassword() {
        AuthorDTO authorWithEmptyPassword = new AuthorDTO();
        authorWithEmptyPassword.setId(2L);
        authorWithEmptyPassword.setFirstName("John");
        authorWithEmptyPassword.setLastName("Smith");
        authorWithEmptyPassword.setPassword("");

        Author authorEntity = new Author();
        when(authorMapper.toEntity(any(AuthorDTO.class))).thenReturn(authorEntity);
        when(authorRepository.save(any(Author.class))).thenReturn(authorEntity);
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorWithEmptyPassword);

        AuthorDTO result = authorService.save(authorWithEmptyPassword);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("", result.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(authorRepository).save(authorEntity);
    }

}
