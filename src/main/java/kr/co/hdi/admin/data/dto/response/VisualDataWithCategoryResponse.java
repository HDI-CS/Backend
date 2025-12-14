package kr.co.hdi.admin.data.dto.response;

import java.util.List;

public record VisualDataWithCategoryResponse(
        String categoryName,
        List<VisualDataResponse> data
) {
}
