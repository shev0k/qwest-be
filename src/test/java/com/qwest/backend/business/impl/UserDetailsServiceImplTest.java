package com.qwest.backend.business.impl;

import com.qwest.backend.domain.Author;
import com.qwest.backend.domain.util.AuthorRole;
import com.qwest.backend.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setEmail("test@example.com");
        author.setPasswordHash("securePassword");
        author.setRole(AuthorRole.FOUNDER);
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        when(authorRepository.findByEmail("test@example.com")).thenReturn(Optional.of(author));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals("test@example.com", userDetails.getUsername(), "Email should match the expected username");

        boolean hasExpectedAuthority = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FOUNDER"));
        assertTrue(hasExpectedAuthority, "User should have ROLE_FOUNDER authority");
    }



    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        when(authorRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("unknown@example.com"));
        verify(authorRepository).findByEmail("unknown@example.com");
    }

    @Test
    void loadUserByUsername_UserHasNoRoles_HandlesNoRoleSituation() {
        author.setRole(null); // Simulate author without a role
        when(authorRepository.findByEmail("test@example.com")).thenReturn(Optional.of(author));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userDetailsService.loadUserByUsername("test@example.com"));
        assertEquals("User has no roles assigned", exception.getMessage());
    }
}
