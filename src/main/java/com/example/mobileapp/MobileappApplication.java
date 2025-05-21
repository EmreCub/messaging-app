package com.example.mobileapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MobileappApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(MobileappApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Clear existing data
        userRepository.deleteAll();

        // Insert sample users with hashed passwords
        User user1 = new User("Alice", "Smith", "alice@example.com", passwordEncoder.encode("alicePassword"));
        User user2 = new User("Bob", "Johnson", "bob@example.com", passwordEncoder.encode("bobPassword"));

        userRepository.save(user1);
        userRepository.save(user2);

        // Fetch and print all users
        System.out.println("All Users:");
        userRepository.findAll().forEach(System.out::println);
    }
}
