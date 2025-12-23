package kr.co.hdi.admin.assignment.dto.query;

public record AssignmentRow(
        Long userId,
        String username,
        Long dataId,
        String dataCode
) {
}
