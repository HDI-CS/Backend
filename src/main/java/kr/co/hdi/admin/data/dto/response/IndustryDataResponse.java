package kr.co.hdi.admin.data.dto.response;

import kr.co.hdi.domain.data.entity.IndustryData;

public record IndustryDataResponse (
    Long id,
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
    String side2ImagePath,
    String side3ImagePath
) {

    public static IndustryDataResponse from (
            IndustryData i,
            String detailImagePath, String frontImagePath, String sideImagePath, String side2ImagePath, String side3ImagePath){

        return new IndustryDataResponse(
                i.getId(),
                i.getOriginalId(),
                i.getProductName(),
                i.getCompanyName(),
                i.getModelName(),
                i.getPrice(),
                i.getMaterial(),
                i.getSize(),
                i.getWeight(),
                i.getReferenceUrl(),
                i.getRegisteredAt(),
                i.getProductPath(),
                i.getProductTypeName(),
                detailImagePath,
                frontImagePath,
                sideImagePath,
                side2ImagePath,
                side3ImagePath
        );
    }
}
