package kr.co.hdi.admin.data.dto.response;

import kr.co.hdi.domain.data.entity.IndustryCategoryFieldSpec;

public record IndustryCategoryFieldSpecResponse(
        Long id,
        String fieldKey,
        String fieldLabel,
        Integer displayOrder
) {
    public static IndustryCategoryFieldSpecResponse from(IndustryCategoryFieldSpec spec) {
        return new IndustryCategoryFieldSpecResponse(
                spec.getId(),
                spec.getFieldKey(),
                spec.getFieldLabel(),
                spec.getDisplayOrder()
        );
    }
}
