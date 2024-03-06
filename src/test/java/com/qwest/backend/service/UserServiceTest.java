package com.qwest.backend.service;

import com.qwest.backend.domain.user.Role;
import com.qwest.backend.domain.user.User;
import com.qwest.backend.repository.UserRepository;
import com.qwest.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("John Doe", "john.doe@example.com", "password123", "image.jpg", Arrays.asList(Role.TRAVELER), "No Preferences");
        user.setId(1L);
    }

    @Test
    void saveUserTest() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User newUser = new User("Jane Doe", "jane.doe@example.com", "password123");
        User created = userService.saveUser(newUser);
        assertNotNull(created);
        assertEquals(newUser.getUsername(), created.getUsername());
        assertEquals(newUser.getEmail(), created.getEmail());
    }


    @Test
    void getUserByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Optional<User> found = userService.getUserById(1L);
        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
    }

    @Test
    void getAllUsersTest() {
        User user2 = new User("Jane Doe", "jane.doe@example.com", "password123");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
    }

    @Test
    void updateUserTest() {
        User updatedUser = new User("John Updated", "john.updated@example.com", "updatedPassword123");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(user.getId(), updatedUser);
        assertNotNull(result);
        assertEquals(updatedUser.getUsername(), result.getUsername());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    void deleteUserTest() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}
