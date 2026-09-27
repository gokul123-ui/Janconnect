package com.janconnect.service;

import com.janconnect.dto.AuthDTO;
import com.janconnect.dto.UserDTO;
import com.janconnect.entity.User;
import com.janconnect.exception.BadRequestException;
import com.janconnect.exception.DuplicateResourceException;
import com.janconnect.exception.UnauthorizedException;
import com.janconnect.repository.UserRepository;
import com.janconnect.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new DuplicateResourceException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {}", saved.getEmail());

        String token = jwtTokenProvider.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        return new AuthDTO.AuthResponse(token, UserDTO.from(saved));
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        log.info("User logged in: {}", user.getEmail());
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthDTO.AuthResponse(token, UserDTO.from(user));
    }

    public UserDTO getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return UserDTO.from(user);
    }
}
