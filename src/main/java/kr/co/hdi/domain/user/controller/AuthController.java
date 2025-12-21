package kr.co.hdi.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import kr.co.hdi.domain.user.dto.RegisterRequest;
import kr.co.hdi.domain.user.dto.request.LoginRequest;
import kr.co.hdi.domain.user.dto.response.AuthResponse;
import kr.co.hdi.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/auth")
@Tag(name = "Authentication", description = "Authentication 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request,
            HttpSession session
    ) {
        log.debug("Login request received for email: {}", request.email());
        AuthResponse response = authService.login(request.email(), request.password());

        session.setAttribute("userId", response.id());
        session.setAttribute("email", response.email());
        session.setAttribute("role", response.role());
        session.setAttribute("name", response.name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @Operation(summary = "새로운 전문가 등록")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.createUser(request.email(), request.password(), request.name(), request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register-admin")
    @Operation(summary = "새로운 어드민 등록")
    public ResponseEntity<AuthResponse> registerAdmin(
            @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.createAdmin(request.email(), request.password(), request.name(),request.type());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
