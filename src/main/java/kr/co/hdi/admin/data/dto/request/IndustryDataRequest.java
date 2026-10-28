package kr.co.hdi.admin.data.dto.request;

import kr.co.hdi.domain.data.enums.IndustryDataCategory;

import java.util.Map;

public record IndustryDataRequest(
        String code,
        String productName,
        String companyName,
        String price,
        String referenceUrl,
        String registeredAt,
        String productPath,
        String productTypeName,
        String weight,

        // 2025
        String modelName,
        String material,
        String size,

        String usage,
        String shoppingUrl,

        // 카테고리마다 다른 스펙 필드 (고정 컬럼이 아닌 key-value). 예: {"extractionPressure": "15bar"}
        Map<String, String> attributes,

        // 이미지
        String originalDetailImagePath,
        String originalFrontImagePath,
        String originalSideImagePath,
        String originalSide2ImagePath,
        String originalSide3ImagePath,
        IndustryDataCategory industryDataCategory
) {
}
