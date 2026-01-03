package kr.co.hdi.survey.dto.response;

import kr.co.hdi.dataset.domain.Brand;
import kr.co.hdi.domain.data.entity.VisualData;

public record BrandDatasetResponse(

        String name,
        String id,
        String sectorCategory,
        String mainProductCategory,
        String mainProduct,
        String target,
        String referenceUrl,
        String image
) {
    public static BrandDatasetResponse fromEntity(VisualData data) {
        return new BrandDatasetResponse(
                data.getBrandName(),
                data.getBrandCode(),
                data.getSectorCategory(),
                data.getMainProductCategory(),
                data.getMainProduct(),
                data.getTarget(),
                data.getReferenceUrl(),
                data.getLogoImage()
        );
    }

    public static BrandDatasetResponse fromEntity(Brand brand) {
        return new BrandDatasetResponse(
                brand.getBrandName(),
                brand.getBrandCode(),
                brand.getSectorCategory(),
                brand.getMainProductCategory(),
                brand.getMainProduct(),
                brand.getTarget(),
                brand.getReferenceUrl(),
                brand.getImage()
        );
    }
}
