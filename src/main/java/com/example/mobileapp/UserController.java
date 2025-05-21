package com.example.mobileapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // User registration endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> requestBody) {
        String firstName = requestBody.get("firstName");
        String lastName = requestBody.get("lastName");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Error: Email already in use!"));
        }

        // Create and save the user
        User newUser = new User(firstName, lastName, email, passwordEncoder.encode(password));
        User savedUser = userRepository.save(newUser);

        // Return user ID and name in response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        response.put("userId", savedUser.getId());
        response.put("name", savedUser.getFirstName() + " " + savedUser.getLastName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // User login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (passwordEncoder.matches(password, existingUser.getPassword())) {
                // Generate JWT token
                String token = jwtUtil.generateToken(email);

                // Return user ID, token, and name in response
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful!");
                response.put("token", token);
                response.put("userId", existingUser.getId());
                response.put("name", existingUser.getFirstName() + " " + existingUser.getLastName());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid password!"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found!"));
        }
    }

    // Endpoint to get all users
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllUsers() {
        List<Map<String, String>> users = userRepository.findAll().stream()
                .map(user -> Map.of(
                        "id", user.getId(),
                        "name", user.getFirstName() + " " + user.getLastName(),
                        "email", user.getEmail()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
