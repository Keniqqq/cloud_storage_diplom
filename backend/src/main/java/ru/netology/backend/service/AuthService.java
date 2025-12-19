package ru.netology.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.backend.exception.UnauthorizedException;
import ru.netology.backend.model.Token;
import ru.netology.backend.exception.InvalidCredentialsException;
import ru.netology.backend.exception.UserNotFoundException;
import ru.netology.backend.model.User;
import ru.netology.backend.repository.TokenRepository;
import ru.netology.backend.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, TokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String login, String password) throws InvalidCredentialsException {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(login);
        Token tokenEntity = new Token();
        tokenEntity.setTokenValue(token);
        tokenEntity.setUserId(user.getId());
        tokenRepository.save(tokenEntity);
        return token;
    }

    public void logout(String token) throws UnauthorizedException {
        Token tokenEntity = tokenRepository.findByTokenValueAndActiveTrue(token)
                .orElseThrow(() -> new UnauthorizedException("Token not found"));
        tokenEntity.setActive(false);
        tokenRepository.save(tokenEntity);
    }
}