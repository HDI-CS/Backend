package kr.co.hdi.admin.data.dto.request;

import java.util.List;

public record IndustryDataIdsRequest(
        List<Long> ids
) {
}
