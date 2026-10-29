package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.enums.VisualDataCategory;

import java.util.Map;

public record VisualDatasetResponse(

        String name,
        String id,
        String sectorCategory,
        String referenceUrl,
        String image,

        Map<String, String> attributes,
        VisualDataCategory visualDataCategory

) {
    public static VisualDatasetResponse fromEntity(VisualData data, String visualDataImage) {
        return new VisualDatasetResponse(
                data.getBrandName(),
                data.getBrandCode(),
                data.getSectorCategory(),
                data.getReferenceUrl(),
                visualDataImage,

                data.getAttributes(),
                data.getVisualDataCategory()

        );
    }
}
