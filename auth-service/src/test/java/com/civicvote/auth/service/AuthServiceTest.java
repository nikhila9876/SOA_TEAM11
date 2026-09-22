package com.civicvote.auth.service;

import com.civicvote.auth.dto.AuthResponse;
import com.civicvote.auth.dto.LoginRequest;
import com.civicvote.auth.dto.RegisterRequest;
import com.civicvote.auth.entity.Role;
import com.civicvote.auth.entity.User;
import com.civicvote.auth.exception.DuplicateUserException;
import com.civicvote.auth.repository.UserRepository;
import com.civicvote.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("testuser", "test@example.com", "password", Role.VOTER);
        loginRequest = new LoginRequest("testuser", "password");
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .role(Role.VOTER)
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("dummy_token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("dummy_token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(Role.VOTER, response.getRole());
    }

    @Test
    void register_ThrowsDuplicateUserException() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> authService.register(registerRequest));
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("dummy_token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("dummy_token", response.getToken());
        assertEquals("testuser", response.getUsername());
    }
}
