package kr.co.hdi.admin.data.dto.request;

import kr.co.hdi.domain.data.enums.VisualDataCategory;

public record VisualDataRequest(
        String code,
        String name,
        String sectorCategory,
        String mainProductCategory,
        String mainProduct,
        String target,
        String referenceUrl,
        String logoImage,
        VisualDataCategory visualDataCategory
) {
}
