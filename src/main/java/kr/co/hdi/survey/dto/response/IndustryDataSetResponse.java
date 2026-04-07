package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;

public record IndustryDataSetResponse(
        String id,
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

        // 2026
        String noiseCancelling,
        String codec,
        String extraFeatures,
        String controlType,
        String waterproof,
        String maxPlayTime,
        String chargeTime,
        String usage,
        String shoppingUrl,
        String soundOutput,
        String connectivity,

        String detailImagePath,
        String frontImagePath,
        String sideImagePath,

        IndustryDataCategory industryCategory
) {

    public static IndustryDataSetResponse fromEntity(
            IndustryData data,
            String detailImagePath,
            String frontImagePath,
            String sideImagePath) {
        return new IndustryDataSetResponse(
                data.getId().toString(),
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

                // 2026
                data.getNoiseCancelling(),
                data.getCodec(),
                data.getExtraFeatures(),
                data.getControlType(),
                data.getWaterproof(),
                data.getMaxPlayTime(),
                data.getChargeTime(),
                data.getUsage(),
                data.getShoppingUrl(),
                data.getSoundOutput(),
                data.getConnectivity(),

                detailImagePath,
                frontImagePath,
                sideImagePath,
                data.getIndustryDataCategory()
        );
    }
}
