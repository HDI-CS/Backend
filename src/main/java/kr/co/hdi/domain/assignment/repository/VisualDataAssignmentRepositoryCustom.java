package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;
import kr.co.hdi.domain.data.entity.VisualData;

import java.time.LocalDateTime;
import java.util.List;

public interface VisualDataAssignmentRepositoryCustom {

    LocalDateTime findLastModifiedAtByAssessmentRound(Long assessmentRoundId);
    List<AssignmentRow> findVisualDataAssignment(Long assessmentRoundId, String q);
    List<AssignmentRow> findVisualDataAssignmentByUser(Long assessmentRoundId, Long userId);

    List<VisualData> findVisualDataByUserAndAssessmentRound(Long userId, Long assessmentRoundId);
}
