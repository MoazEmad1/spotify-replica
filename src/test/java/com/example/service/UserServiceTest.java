package com.example.service;

import com.example.mapper.UserMapper;
import com.example.domain.User;
import com.example.dto.ReadUserDTO;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private Map<String, Object> attributes;
    private User user;
    private ReadUserDTO readUserDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("test@example.com");
        user.setLastModifiedDate(Instant.now());

        // Initialize the readUserDTO at class level
        readUserDTO = new ReadUserDTO("John", "Doe", "john.doe@example.com", "https://example.com/image.jpg");

        attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("given_name", "Test");
        attributes.put("family_name", "User");

        when(oAuth2User.getAttributes()).thenReturn(attributes);
    }

    @Test
    void testSyncWithIdp_NewUser() {
        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.empty());

        userService.syncWithIdp(oAuth2User);

        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void testSyncWithIdp_UpdateExistingUser() {
        attributes.put("updated_at", Instant.now().plusSeconds(1000));
        when(userRepository.findOneByEmail(anyString())).thenReturn(Optional.of(user));

        userService.syncWithIdp(oAuth2User);

        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void testGetAuthenticatedUserFromSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(userMapper.readUserDTOToUser(any(User.class))).thenReturn(readUserDTO);
        SecurityContextHolder.setContext(securityContext);

        ReadUserDTO result = userService.getAuthenticatedUserFromSecurityContext();

        assertNotNull(result);
        verify(userMapper, times(1)).readUserDTOToUser(any(User.class));
    }

    @Test
    void testIsAuthenticated_True() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        SecurityContextHolder.setContext(securityContext);

        boolean isAuthenticated = userService.isAuthenticated();

        assertTrue(isAuthenticated);
    }

    @Test
    void testIsAuthenticated_False() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        boolean isAuthenticated = userService.isAuthenticated();

        assertFalse(isAuthenticated);
    }

    @Test
    void testGetByEmail_UserFound() {
        // Mock repository to return a valid User
        when(userRepository.findOneByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Mock mapper to return a valid ReadUserDTO
        when(userMapper.readUserDTOToUser(user)).thenReturn(readUserDTO);

        // Call the method
        Optional<ReadUserDTO> result = userService.getByEmail("test@example.com");

        // Assert that result is present
        assertTrue(result.isPresent(), "Expected result to be present, but it was empty.");
    }

    @Test
    void testGetByEmail_UserNotFound() {
        when(userRepository.findOneByEmail("test@example.com")).thenReturn(Optional.empty());

        Optional<ReadUserDTO> result = userService.getByEmail("test@example.com");

        assertFalse(result.isPresent());
        verify(userMapper, never()).readUserDTOToUser(any(User.class));
    }
}
