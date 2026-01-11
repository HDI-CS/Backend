package kr.co.hdi.admin.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import kr.co.hdi.domain.user.dto.response.AuthResponse;
import kr.co.hdi.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
@Tag(name = "Admin", description = "Admin 유저 관련 API")
public class AdminController {

    private final AuthService authService;

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 어드민 유저의 정보 반환")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        AuthResponse response = authService.getAdminInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
