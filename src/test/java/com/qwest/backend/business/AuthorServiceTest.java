package com.qwest.backend.business;

import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.repository.mapper.AuthorMapper;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.business.impl.AuthorServiceImpl;
import com.qwest.backend.configuration.security.token.JwtUtil;
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

    @Mock
    private JwtUtil jwtUtil;

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

        lenient().when(jwtUtil.generateToken(anyString())).thenReturn("mock-jwt-token");
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
        // Setup
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(authorMapper.toEntity(any(AuthorDTO.class))).thenReturn(author);
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);
        when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");
        when(jwtUtil.generateToken(anyString())).thenReturn("mock-jwt-token");

        authorDTO.setPassword("password");

        AuthorDTO savedAuthor = authorService.save(authorDTO);

        assertNotNull(savedAuthor, "The saved AuthorDTO should not be null.");
        assertEquals("mock-jwt-token", savedAuthor.getJwt(), "JWT token should be 'mock-jwt-token'.");
        assertEquals(authorDTO.getFirstName(), savedAuthor.getFirstName(), "First name should match the DTO provided.");

        verify(passwordEncoder).encode("password");
        verify(authorRepository).save(any(Author.class));
        verify(jwtUtil).generateToken(authorDTO.getEmail());
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

    @Test
    void login_Successful_ReturnsAuthorDTOWithJwt() {
        when(authorRepository.findByEmail(authorDTO.getEmail())).thenReturn(Optional.of(author));
        when(passwordEncoder.matches(authorDTO.getPassword(), author.getPasswordHash())).thenReturn(true);
        when(authorMapper.toDto(author)).thenReturn(authorDTO);

        Optional<AuthorDTO> result = authorService.login(authorDTO);

        assertTrue(result.isPresent(), "Login should succeed and return an AuthorDTO");
        assertNotNull(result.get().getJwt(), "JWT should be set in the DTO");
        assertEquals("mock-jwt-token", result.get().getJwt(), "JWT should match the expected token");

        verify(jwtUtil).generateToken(authorDTO.getEmail());
    }
    @Test
    void login_NoUserFound_ReturnsEmpty() {
        when(authorRepository.findByEmail(authorDTO.getEmail())).thenReturn(Optional.empty());

        Optional<AuthorDTO> result = authorService.login(authorDTO);

        assertTrue(result.isEmpty(), "Login should fail and return an empty Optional");

        verify(authorRepository).findByEmail(authorDTO.getEmail());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void findByEmail_UserFound_ReturnsAuthorDTO() {
        // Setup
        Author author = new Author();
        author.setEmail("user@example.com");
        AuthorDTO expectedDto = new AuthorDTO();
        expectedDto.setEmail("user@example.com");

        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(expectedDto);

        // Execute
        Optional<AuthorDTO> result = authorService.findByEmail("user@example.com");

        // Verify
        assertTrue(result.isPresent(), "AuthorDTO should be present");
        assertEquals("user@example.com", result.get().getEmail(), "Email should match");
    }

    @Test
    void findByEmail_UserNotFound_ReturnsEmpty() {
        // Setup
        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // Execute
        Optional<AuthorDTO> result = authorService.findByEmail("user@example.com");

        // Verify
        assertFalse(result.isPresent(), "Result should be empty");
    }

    @Test
    void login_UserNotFound_ReturnsEmpty() {
        // Setup
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("user@example.com");
        authorDTO.setPassword("password");
        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // Execute
        Optional<AuthorDTO> result = authorService.login(authorDTO);

        // Verify
        assertFalse(result.isPresent(), "Should return empty Optional");
    }

    @Test
    void login_PasswordMismatch_ReturnsEmpty() {
        // Setup
        Author author = new Author();
        author.setEmail("user@example.com");
        author.setPasswordHash("hashedPassword");
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("user@example.com");
        authorDTO.setPassword("password");
        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(false);

        // Execute
        Optional<AuthorDTO> result = authorService.login(authorDTO);

        // Verify
        assertFalse(result.isPresent(), "Should return empty Optional");
    }


}
