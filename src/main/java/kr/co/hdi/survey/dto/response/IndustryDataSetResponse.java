package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.data.entity.IndustryData;

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

        String detailImagePath,
        String frontImagePath,
        String sideImagePath
) {

    public static IndustryDataSetResponse fromEntity(IndustryData data) {
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
                data.getDetailImagePath(),
                data.getFrontImagePath(),
                data.getSideImagePath()
        );
    }
}
