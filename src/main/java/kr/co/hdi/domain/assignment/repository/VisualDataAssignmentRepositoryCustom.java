package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;

import java.util.List;

public interface VisualDataAssignmentRepositoryCustom {

    List<AssignmentRow> findVisualDataAssignment(Long assessmentRoundId);
    List<AssignmentRow> findVisualDataAssignmentByUser(Long assessmentRoundId, Long userId);
}
