package kr.co.hdi.admin.assignment.dto.response;

import java.util.List;

public record AssignmentResponse(
        Long memberId,
        String name,
        List<AssignmentDataResponse> dataIds
) {
}
