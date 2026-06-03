package com.zamazor.market.modules.auth.controller;

import com.zamazor.market.modules.auth.models.dto.RegisterRequest;
import com.zamazor.market.modules.auth.service.AuthenticationService;
import com.zamazor.market.modules.user.models.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterRequest request, UriComponentsBuilder uriBuilder) {
        var response = authenticationService.register(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

}
