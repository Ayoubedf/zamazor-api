package com.zamazor.market.modules.auth.service;

import com.zamazor.market.modules.auth.models.dto.RegisterRequest;
import com.zamazor.market.modules.user.models.mapper.UserMapper;
import com.zamazor.market.modules.user.models.dto.UserDto;
import com.zamazor.market.modules.user.models.entity.User;
import com.zamazor.market.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(Objects.requireNonNull(passwordEncoder.encode(request.getPassword())))
                .name(request.getName())
                .avatarUrl(null)
                .birthDate(request.getBirthDate())
                .isAdmin(false)
                .build();
        return userMapper.toDto(userRepository.save(user));
    }
}
