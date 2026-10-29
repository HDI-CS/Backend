package kr.co.hdi.admin.data.dto.response;

import kr.co.hdi.domain.data.entity.VisualCategoryFieldSpec;

public record VisualCategoryFieldSpecResponse(
        Long id,
        String fieldKey,
        String fieldLabel,
        Integer displayOrder
) {
    public static VisualCategoryFieldSpecResponse from(VisualCategoryFieldSpec spec) {
        return new VisualCategoryFieldSpecResponse(
                spec.getId(),
                spec.getFieldKey(),
                spec.getFieldLabel(),
                spec.getDisplayOrder()
        );
    }
}
