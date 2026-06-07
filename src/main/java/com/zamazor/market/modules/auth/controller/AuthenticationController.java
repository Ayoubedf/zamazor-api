package com.zamazor.market.modules.auth.controller;

import com.zamazor.market.modules.auth.models.dto.*;
import com.zamazor.market.modules.auth.models.mapper.AuthenticationMapper;
import com.zamazor.market.modules.auth.service.AuthenticationService;
import com.zamazor.market.modules.auth.util.CookieUtility;
import com.zamazor.market.modules.user.models.dto.UserDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CookieUtility cookieUtility;
		private final AuthenticationMapper authenticationMapper;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterRequest request, UriComponentsBuilder uriBuilder) {
        var response = authenticationService.register(request);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request, HttpServletResponse response) {
        AuthenticationResult authenticationResult = authenticationService.authenticate(request);
        ResponseCookie cookie = cookieUtility.createRefreshTokenCookie(authenticationResult.getRefreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(authenticationMapper.toAuthenticationResponse(authenticationResult));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        return ResponseEntity.ok(authenticationService.getCurrentUser());
    }

}
