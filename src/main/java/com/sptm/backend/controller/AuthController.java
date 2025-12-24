package com.sptm.backend.controller;

import com.sptm.backend.dto.AuthResponse;
import com.sptm.backend.dto.LoginRequest;
import com.sptm.backend.dto.RegisterRequest;
import com.sptm.backend.model.User;
import com.sptm.backend.repository.UserRepository;
import com.sptm.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // In a real app we would use AuthenticationManager to validate
        // username/password via UserDetailsService.
        // For this simplified implementation:
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPasswordHash().equals(loginRequest.getPassword())) { // Hashing omitted for brevity
            return ResponseEntity.badRequest().body("Invalid password");
        }

        String jwt = jwtUtils.generateJwtToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPasswordHash(signUpRequest.getPassword()); // Should hash this!

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody java.util.Map<String, String> payload) {
        String email = payload.get("email");
        // String name = payload.get("name");
        // String photoUrl = payload.get("photoUrl"); // Optional, if we add photo to
        // User model later

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Register new user
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // Use email as username for simplicity and uniqueness
            user.setPasswordHash("GOOGLE_AUTH_USER"); // Placeholder for Google users
            userRepository.save(user);
        }

        String jwt = jwtUtils.generateJwtToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail()));
    }
}
