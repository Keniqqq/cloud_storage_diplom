package ru.netology.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.backend.model.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenValueAndActiveTrue(String token);
}