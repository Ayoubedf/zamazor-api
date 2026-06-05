package com.zamazor.market.modules.auth.service;

import com.zamazor.market.config.JwtService;
import com.zamazor.market.modules.auth.exception.EmailAlreadyExistsException;
import com.zamazor.market.modules.auth.models.dto.AuthenticationRequest;
import com.zamazor.market.modules.auth.models.dto.AuthenticationResponse;
import com.zamazor.market.modules.auth.models.dto.RegisterRequest;
import com.zamazor.market.modules.user.models.mapper.UserMapper;
import com.zamazor.market.modules.user.models.dto.UserDto;
import com.zamazor.market.modules.user.models.entity.User;
import com.zamazor.market.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }
        var user = userMapper.toEntity(request);
        user.setPassword(Objects.requireNonNull(passwordEncoder.encode(request.getPassword())));
        user.setIsAdmin(false);
        return userMapper.toDto(userRepository.save(user));
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new BadCredentialsException("Invalid Credentials");
        }
        var userDto = userMapper.toDto(user);
        var token = jwtService.generateToken(user);
        return new AuthenticationResponse(token, userDto);
    }


    public UserDto me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new BadCredentialsException("User not authenticated");
        }

        return userMapper.toDto(user);
    }
}
