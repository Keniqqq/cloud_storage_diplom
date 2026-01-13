package ru.netology.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.backend.exception.InvalidCredentialsException;
import ru.netology.backend.exception.UnauthorizedException;
import ru.netology.backend.exception.UserNotFoundException;
import ru.netology.backend.model.Token;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.TokenRepository;
import ru.netology.backend.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, tokenRepository, passwordEncoder, jwtService);
    }

    @Test
    void shouldLoginSuccessfully() throws InvalidCredentialsException {
        String login = "user";
        String password = "password";
        String encodedPassword = "encoded";
        String token = "jwt-token";

        User user = new User(login, encodedPassword);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(login)).thenReturn(token);

        String result = authService.login(login, password);

        assertEquals(token, result);
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void shouldThrowUserNotFoundExceptionOnLogin() {
        String login = "user";
        String password = "password";

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(login, password));
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionOnLogin() {
        String login = "user";
        String password = "wrong";
        String encodedPassword = "encoded";

        User user = new User(login, encodedPassword);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(login, password));
    }

    @Test
    void shouldLogoutSuccessfully() throws UnauthorizedException {
        String tokenValue = "valid-token";
        Token token = new Token(tokenValue, 1L);
        when(tokenRepository.findByTokenValueAndActiveTrue(tokenValue)).thenReturn(Optional.of(token));

        authService.logout(tokenValue);

        verify(tokenRepository, times(1)).save(token);
        assertFalse(token.isActive());
    }

    @Test
    void shouldThrowUnauthorizedExceptionOnLogout() {
        String tokenValue = "invalid-token";
        when(tokenRepository.findByTokenValueAndActiveTrue(tokenValue)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.logout(tokenValue));
    }
}