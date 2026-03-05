package com.ecommerce.infrastructure.adapter.in.rest;

import com.ecommerce.application.dto.AuthResponse;
import com.ecommerce.application.dto.LoginRequest;
import com.ecommerce.application.dto.MessageResponse;
import com.ecommerce.application.dto.RegisterRequest;
import com.ecommerce.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Register, login and logout")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already in use")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token valid for 24 hours.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "JWT is stateless — logout is handled client-side by discarding the token.")
    @ApiResponse(responseCode = "200", description = "Logout acknowledged")
    public ResponseEntity<MessageResponse> logout() {
        return ResponseEntity.ok(MessageResponse.of("Logged out successfully"));
    }
}
