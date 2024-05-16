package com.qwest.backend.business;

import com.qwest.backend.business.impl.AuthorServiceImpl;
import com.qwest.backend.configuration.exceptionhandler.FileNotFoundException;
import com.qwest.backend.configuration.exceptionhandler.FileStorageException;
import com.qwest.backend.configuration.security.token.JwtUtil;
import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.dto.AuthorDTO;
import com.qwest.backend.repository.AuthorRepository;
import com.qwest.backend.repository.mapper.AuthorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

    @Mock
    private FileStorageService fileStorageService;

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
        author.setPasswordHash("oldHashedPassword");

        authorDTO = new AuthorDTO();
        authorDTO.setId(1L);
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");
        authorDTO.setEmail("john.doe@example.com");
        authorDTO.setPassword("newPassword");

        lenient().when(jwtUtil.generateToken(anyString())).thenReturn("mock-jwt-token");
        lenient().when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        lenient().when(passwordEncoder.matches("newPassword", "oldHashedPassword")).thenReturn(false);
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
        Author author = new Author();
        author.setEmail("user@example.com");
        AuthorDTO expectedDto = new AuthorDTO();
        expectedDto.setEmail("user@example.com");

        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(expectedDto);

        Optional<AuthorDTO> result = authorService.findByEmail("user@example.com");

        assertTrue(result.isPresent(), "AuthorDTO should be present");
        assertEquals("user@example.com", result.get().getEmail(), "Email should match");
    }

    @Test
    void findByEmail_UserNotFound_ReturnsEmpty() {
        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Optional<AuthorDTO> result = authorService.findByEmail("user@example.com");

        assertFalse(result.isPresent(), "Result should be empty");
    }

    @Test
    void login_UserNotFound_ReturnsEmpty() {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("user@example.com");
        authorDTO.setPassword("password");
        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        Optional<AuthorDTO> result = authorService.login(authorDTO);

        assertFalse(result.isPresent(), "Should return empty Optional");
    }

    @Test
    void login_PasswordMismatch_ReturnsEmpty() {
        Author author = new Author();
        author.setEmail("user@example.com");
        author.setPasswordHash("hashedPassword");
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("user@example.com");
        authorDTO.setPassword("password");
        when(authorRepository.findByEmail("user@example.com")).thenReturn(Optional.of(author));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(false);

        Optional<AuthorDTO> result = authorService.login(authorDTO);

        assertFalse(result.isPresent(), "Should return empty Optional");
    }

    @Test
    void updateAuthor_Success_NoEmailChange() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setEmail("john.doe@example.com");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("john.doe@example.com");
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);

        AuthorDTO result = authorService.update(authorId, authorDTO);

        assertNull(result.getJwt(), "JWT should not be generated when the email hasn't changed.");
        verify(authorRepository).save(any(Author.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void updateAuthor_Success_EmailChange() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setEmail("old.email@example.com");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("new.email@example.com");
        authorDTO.setFirstName("John");
        authorDTO.setLastName("Doe");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);
        when(jwtUtil.generateToken("new.email@example.com")).thenReturn("newToken");

        AuthorDTO result = authorService.update(authorId, authorDTO);

        assertThat(result.getJwt(), is("newToken"));
        verify(authorRepository).save(any(Author.class));
        verify(jwtUtil).generateToken("new.email@example.com");
    }

    @Test
    void updateAuthor_NotFound() {
        Long nonExistentAuthorId = 99L;
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("does.not.exist@example.com");

        when(authorRepository.findById(nonExistentAuthorId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            authorService.update(nonExistentAuthorId, authorDTO);
        });
    }

    @Test
    void updateAuthor_Success_PasswordChange() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setEmail("old.email@example.com");
        existingAuthor.setPasswordHash("oldHashedPassword");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setEmail("new.email@example.com");
        authorDTO.setPassword("newPassword");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(passwordEncoder.matches("newPassword", "oldHashedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(authorMapper.toDto(any(Author.class))).thenReturn(new AuthorDTO());

        AuthorDTO result = authorService.update(authorId, authorDTO);

        assertNotNull(result);
        verify(passwordEncoder).encode("newPassword");
        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void updateAuthorWithPasswordChangeConditionally() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setEmail("john.doe@example.com");
        existingAuthor.setPasswordHash("oldHashedPassword");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setPassword("newPassword");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(passwordEncoder.matches("newPassword", "oldHashedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(authorMapper.toDto(existingAuthor)).thenReturn(authorDTO);

        AuthorDTO result = authorService.update(authorId, authorDTO);

        assertNotNull(result);
        assertEquals("newHashedPassword", existingAuthor.getPasswordHash());
        verify(passwordEncoder).matches("newPassword", "oldHashedPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void updateAuthorEmptyPassword() {
        Long authorId = 1L;
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setPassword("");

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            authorService.update(authorId, authorDTO);
        });

        assertEquals("Author not found with id " + authorId, exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void updateAuthorWithoutPasswordChangeDueToMatch() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setEmail("john.doe@example.com");
        existingAuthor.setPasswordHash("oldHashedPassword");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setPassword("newPassword");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(passwordEncoder.matches("newPassword", "oldHashedPassword")).thenReturn(true);
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(authorMapper.toDto(existingAuthor)).thenReturn(authorDTO);

        AuthorDTO result = authorService.update(authorId, authorDTO);

        assertNotNull(result);
        assertEquals("oldHashedPassword", existingAuthor.getPasswordHash());
        verify(passwordEncoder).matches("newPassword", "oldHashedPassword");
        verify(passwordEncoder, never()).encode("newPassword");
        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void updateAuthorWithEmptyPasswordShouldNotUpdatePasswordHash() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setEmail("john.doe@example.com");
        existingAuthor.setPasswordHash("existingHashedPassword");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setPassword("");

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(authorMapper.toDto(existingAuthor)).thenReturn(authorDTO);

        AuthorDTO result = authorService.update(authorId, authorDTO);

        assertNotNull(result);
        assertEquals("existingHashedPassword", existingAuthor.getPasswordHash());
        verify(passwordEncoder, never()).encode("");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void updateAuthorWithNullPasswordShouldNotUpdatePasswordHash() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setEmail("john.doe@example.com");
        existingAuthor.setPasswordHash("existingHashedPassword");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setPassword(null);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(authorMapper.toDto(existingAuthor)).thenReturn(authorDTO);

        AuthorDTO result = authorService.update(authorId, authorDTO);

        assertNotNull(result);
        assertEquals("existingHashedPassword", existingAuthor.getPasswordHash());
        verify(passwordEncoder, never()).encode(null);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void updateAvatar_Success() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setAvatar("oldAvatarUrl");

        MockMultipartFile newAvatarFile = new MockMultipartFile("file", "newAvatar.png", "image/png", "new avatar".getBytes());

        String newAvatarUrl = "https://example.com/newAvatar.png";
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(fileStorageService.uploadFile(any(MultipartFile.class))).thenReturn(newAvatarUrl);
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);

        AuthorDTO result = authorService.updateAvatar(authorId, newAvatarFile);

        assertNotNull(result);
        assertEquals(newAvatarUrl, existingAuthor.getAvatar());
        verify(fileStorageService).deleteFile("oldAvatarUrl");
        verify(fileStorageService).uploadFile(newAvatarFile);
        verify(authorRepository).save(existingAuthor);
        verify(authorMapper).toDto(existingAuthor);
    }

    @Test
    void updateAvatar_AuthorNotFound() {
        Long nonExistentAuthorId = 99L;
        MockMultipartFile avatarFile = new MockMultipartFile("file", "avatar.png", "image/png", "avatar".getBytes());

        when(authorRepository.findById(nonExistentAuthorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            authorService.updateAvatar(nonExistentAuthorId, avatarFile);
        });

        assertEquals("Author not found with id " + nonExistentAuthorId, exception.getMessage());
        verify(authorRepository, never()).save(any(Author.class));
        verify(fileStorageService, never()).uploadFile(any(MultipartFile.class));
    }

    @Test
    void updateAvatar_EmptyOrNullFile() {
        Long authorId = 1L;
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        assertThrows(IllegalArgumentException.class, () -> {
            authorService.updateAvatar(authorId, emptyFile);
        });

        verify(fileStorageService, never()).uploadFile(any(MultipartFile.class));
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void updateAvatar_FileStorageException() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        MockMultipartFile newAvatarFile = new MockMultipartFile("file", "newAvatar.png", "image/png", "new avatar".getBytes());

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(fileStorageService.uploadFile(any(MultipartFile.class))).thenThrow(new FileStorageException("Failed to upload"));

        assertThrows(FileStorageException.class, () -> {
            authorService.updateAvatar(authorId, newAvatarFile);
        });

        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void updateAvatar_FileNotFoundException() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setAvatar("oldAvatarUrl");

        MockMultipartFile newAvatarFile = new MockMultipartFile("file", "newAvatar.png", "image/png", "new avatar".getBytes());

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        doThrow(new FileNotFoundException("File not found")).when(fileStorageService).deleteFile(anyString());

        Exception exception = assertThrows(FileNotFoundException.class, () -> {
            authorService.updateAvatar(authorId, newAvatarFile);
        });

        assertEquals("File not found", exception.getMessage());
        verify(authorRepository, never()).save(any(Author.class));
        verify(fileStorageService).deleteFile("oldAvatarUrl");
        verify(fileStorageService, never()).uploadFile(newAvatarFile);
    }

    @Test
    void updateAvatar_FileIsNull() {
        Long authorId = 1L;
        MultipartFile avatarFile = null;

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        assertThrows(IllegalArgumentException.class, () -> {
            authorService.updateAvatar(authorId, avatarFile);
        });

        verify(fileStorageService, never()).uploadFile(any(MultipartFile.class));
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void updateAvatar_FileIsEmpty() {
        Long authorId = 1L;
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        assertThrows(IllegalArgumentException.class, () -> {
            authorService.updateAvatar(authorId, emptyFile);
        });

        verify(fileStorageService, never()).uploadFile(any(MultipartFile.class));
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void updateAvatar_ExistingAvatarIsEmpty() {
        Long authorId = 1L;
        Author existingAuthor = new Author();
        existingAuthor.setId(authorId);
        existingAuthor.setAvatar("");

        MockMultipartFile newAvatarFile = new MockMultipartFile("file", "newAvatar.png", "image/png", "new avatar".getBytes());

        String newAvatarUrl = "https://example.com/newAvatar.png";
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(fileStorageService.uploadFile(any(MultipartFile.class))).thenReturn(newAvatarUrl);
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(authorMapper.toDto(any(Author.class))).thenReturn(authorDTO);

        AuthorDTO result = authorService.updateAvatar(authorId, newAvatarFile);

        assertNotNull(result);
        assertEquals(newAvatarUrl, existingAuthor.getAvatar());
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(fileStorageService).uploadFile(newAvatarFile);
        verify(authorRepository).save(existingAuthor);
        verify(authorMapper).toDto(existingAuthor);
    }

}
