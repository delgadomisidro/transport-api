
package transport.transport_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import transport.transport_api.dto.LoginRequest;
import transport.transport_api.dto.LoginResponse;
import transport.transport_api.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @Operation(summary = "User login and generate jwt token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
            authService.login(request)
        );
    }
}
