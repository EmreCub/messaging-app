package com.example.mobileapp;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    // Find user by email
    Optional<User> findByEmail(String email);

    // Search by first name, last name, or email (case-insensitive)
    List<User> findByFirstNameRegexIgnoreCaseOrLastNameRegexIgnoreCaseOrEmailRegexIgnoreCase(
        String firstNameRegex, String lastNameRegex, String emailRegex
    );
}
