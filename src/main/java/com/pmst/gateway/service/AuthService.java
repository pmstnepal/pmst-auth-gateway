package com.pmst.gateway.service;

import com.pmst.gateway.model.GatewayUser;
import com.pmst.gateway.model.dto.AuthResponse;
import com.pmst.gateway.model.dto.LoginRequest;
import com.pmst.gateway.model.dto.RefreshRequest;
import com.pmst.gateway.model.dto.RegisterRequest;
import com.pmst.gateway.model.dto.UserDto;
import com.pmst.gateway.repository.GatewayUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final GatewayUserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long expiration;

    public AuthService(GatewayUserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponse login(LoginRequest request) {
        GatewayUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return generateAuthResponse(user);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());
        String displayName = request.getDisplayName() != null ? request.getDisplayName() : request.getUsername();

        GatewayUser user = new GatewayUser(
                request.getEmail(),
                passwordHash,
                request.getUsername(),
                displayName,
                "user"
        );

        user = userRepository.save(user);
        return generateAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        if (!jwtService.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        UUID userId = jwtService.extractUserId(request.getRefreshToken());
        GatewayUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return generateAuthResponse(user);
    }

    public UserDto getUserFromToken(String token) {
        if (!jwtService.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        UUID userId = jwtService.extractUserId(token);
        GatewayUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDto(
                user.getId().toString(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                "active"
        );
    }

    private AuthResponse generateAuthResponse(GatewayUser user) {
        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );

        String idToken = accessToken; // For local dev, idToken and accessToken are the same
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        UserDto userDto = new UserDto(
                user.getId().toString(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole(),
                "active"
        );

        return new AuthResponse(
                idToken,
                accessToken,
                refreshToken,
                (int) (expiration / 1000),
                "Bearer",
                userDto
        );
    }
}
