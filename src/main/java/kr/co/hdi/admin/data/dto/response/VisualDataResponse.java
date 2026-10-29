package kr.co.hdi.admin.data.dto.response;

import kr.co.hdi.domain.data.entity.VisualData;

import java.util.Map;

public record VisualDataResponse(
        Long id,
        String code,
        String name,
        String sectorCategory,
        String referenceUrl,

        Map<String, String> attributes,

        String logoImage
) {

    public static VisualDataResponse from(VisualData v, String image) {
        return new VisualDataResponse(
                v.getId(),
                v.getBrandCode(),
                v.getBrandName(),
                v.getSectorCategory(),
                v.getReferenceUrl(),

                v.getAttributes(),

                image
        );
    }
}
