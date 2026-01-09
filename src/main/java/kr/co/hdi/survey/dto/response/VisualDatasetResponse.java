package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.data.entity.VisualData;

public record VisualDatasetResponse(

        String name,
        String id,
        String sectorCategory,
        String mainProductCategory,
        String mainProduct,
        String target,
        String referenceUrl,
        String image
) {
    public static VisualDatasetResponse fromEntity(VisualData data, String visualDataImage) {
        return new VisualDatasetResponse(
                data.getBrandName(),
                data.getBrandCode(),
                data.getSectorCategory(),
                data.getMainProductCategory(),
                data.getMainProduct(),
                data.getTarget(),
                data.getReferenceUrl(),
                visualDataImage
        );
    }
}
