package kr.co.hdi.admin.data.dto.request;

import kr.co.hdi.domain.data.enums.IndustryDataCategory;

public record IndustryDataRequest(
        String code,
        String productName,
        String companyName,
        String modelName,
        String price,
        String material,
        String size,
        String weight,
        String referenceUrl,
        String registeredAt,
        String productPath,
        String productTypeName,
        String detailImagePath,
        String frontImagePath,
        String sideImagePath,
        IndustryDataCategory industryDataCategory
) {
}
