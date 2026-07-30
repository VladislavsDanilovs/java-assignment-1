package com.swisscom.task_manager.service;

import com.swisscom.task_manager.entity.UserEntity;
import com.swisscom.task_manager.model.AuthRequestDTO;
import com.swisscom.task_manager.model.AuthResponseDTO;
import com.swisscom.task_manager.repository.UserRepository;
import com.swisscom.task_manager.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponseDTO register(AuthRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        UserEntity user = new UserEntity(dto.email(), encodedPassword);

        user.setCreatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(savedUser.getId(), savedUser.getEmail());
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO login(AuthRequestDTO dto) {
        UserEntity user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getEmail());
        return new AuthResponseDTO(token);
    }
}