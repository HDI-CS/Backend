package kr.co.hdi.survey.dto.response;

import kr.co.hdi.crawl.domain.Product;
import kr.co.hdi.crawl.domain.ProductImage;
import kr.co.hdi.domain.data.entity.IndustryData;

public record ProductDataSetResponse(
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

    public static ProductDataSetResponse fromEntity(IndustryData data) {
        return new ProductDataSetResponse(
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

    public static ProductDataSetResponse from(Product product, ProductImage image) {
        return new ProductDataSetResponse(
                product.getId().toString(),
                product.getProductName(),
                product.getCompanyName(),
                product.getModelName(),
                product.getPrice(),
                product.getMaterial(),
                product.getSize(),
                product.getWeight(),
                product.getReferenceUrl(),
                product.getRegisteredAt(),
                product.getProductPath(),
                product.getProductTypeName(),
                image.getDetailPath(),
                image.getFrontPath(),
                image.getSidePath()
        );
    }

}
