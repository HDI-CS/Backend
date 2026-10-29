package kr.co.hdi.admin.data.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VisualCategoryFieldSpecRequest(
        @NotBlank String fieldKey,
        @NotBlank String fieldLabel
) {
}
