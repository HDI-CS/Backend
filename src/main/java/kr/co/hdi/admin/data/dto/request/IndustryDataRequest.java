package kr.co.hdi.admin.data.dto.request;

import kr.co.hdi.domain.data.enums.IndustryDataCategory;

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

        // 2026
        String noiseCancelling,
        String codec,
        String extraFeatures,
        String controlType,
        String waterproof,
        String maxPlayTime,
        String chargeTime,

        // 이미지
        String originalDetailImagePath,
        String originalFrontImagePath,
        String originalSideImagePath,
        String originalSide2ImagePath,
        String originalSide3ImagePath,
        IndustryDataCategory industryDataCategory
) {
}
