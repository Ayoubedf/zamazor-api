package com.zamazor.market.modules.user.service;

import com.zamazor.market.modules.user.models.dto.UserDto;
import com.zamazor.market.modules.user.models.mapper.UserMapper;
import com.zamazor.market.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUser(UUID id) {
        return userRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
