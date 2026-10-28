package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;

import java.util.Map;

public record IndustryDataSetResponse(
        String id,
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

        String usage,
        String shoppingUrl,

        Map<String, String> attributes,

        String detailImagePath,
        String frontImagePath,
        String sideImagePath,
        String side2ImagePath,
        String side3ImagePath,
        IndustryDataCategory industryDataCategory
) {

    public static IndustryDataSetResponse fromEntity(
            IndustryData data,
            String detailImagePath,
            String frontImagePath,
            String sideImagePath,
            String side2ImagePath,
            String side3ImagePath
    ) {
        return new IndustryDataSetResponse(
                data.getId().toString(),
                data.getOriginalId(),
                data.getProductName(),
                data.getCompanyName(),
                data.getModelName(),
                data.getPrice(),
                data.getMaterial(),
                data.getSize(),
                data.getWeight(),
                data.getReferenceUrl(),
                data.getRegisteredAt(),
                data.getProductPath(),
                data.getProductTypeName(),

                data.getUsage(),
                data.getShoppingUrl(),

                data.getAttributes(),

                detailImagePath,
                frontImagePath,
                sideImagePath,
                side2ImagePath,
                side3ImagePath,
                data.getIndustryDataCategory()

                );
    }
}
