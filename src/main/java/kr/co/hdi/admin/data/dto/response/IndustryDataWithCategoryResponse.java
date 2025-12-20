package kr.co.hdi.admin.data.dto.response;

import java.util.List;

public record IndustryDataWithCategoryResponse(
        String categoryName,
        List<IndustryDataResponse> data
) {
}
