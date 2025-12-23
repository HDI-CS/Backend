package kr.co.hdi.admin.assignment.dto.request;

import java.util.List;

public record AssignmentDataRequest(
        Long memberId,
        List<Long> datasetsIds
) {
}
