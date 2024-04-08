package com.qwest.backend.controller;

import com.qwest.backend.domain.user.User;
import com.qwest.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsersTest() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john");
        user1.setEmail("john@example.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("john")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")));
    }

    @Test
    void getUserByIdTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void createUserTest() throws Exception {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPassword("password");

        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\",\"email\":\"new@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("new@example.com")));
    }

    @Test
    void updateUserTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("updatedUser");
        user.setEmail("update@example.com");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUser\",\"email\":\"update@example.com\",\"password\":\"newPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updatedUser")))
                .andExpect(jsonPath("$.email", is("update@example.com")));
    }

    @Test
    void updateUser_Successful() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("updatedUser");
        user.setEmail("update@example.com");

        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUser\",\"email\":\"update@example.com\",\"password\":\"newPass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("updatedUser")))
                .andExpect(jsonPath("$.email", is("update@example.com")));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("updatedUser");
        user.setEmail("update@example.com");

        when(userService.updateUser(eq(userId), any(User.class))).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updatedUser\",\"email\":\"update@example.com\",\"password\":\"newPass\"}"))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/{id}", 1))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}
