package com.sptm.backend.config;

import com.sptm.backend.model.User;
import com.sptm.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User user = new User();
                user.setUsername("testuser");
                user.setEmail("test@example.com");
                user.setPasswordHash("hashedpassword");
                userRepository.save(user);
                System.out.println("Default user created: testuser (ID: 1)");
            }
        };
    }
}
