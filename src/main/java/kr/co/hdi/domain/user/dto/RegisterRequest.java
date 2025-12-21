package kr.co.hdi.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import kr.co.hdi.domain.user.entity.UserType;

public record RegisterRequest(
        @NotBlank
        String email,
        @NotBlank
        String password,
        @NotBlank
        String name,

        UserType type
) {
}
