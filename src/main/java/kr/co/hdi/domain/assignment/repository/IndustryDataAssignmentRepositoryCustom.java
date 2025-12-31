package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;

import java.time.LocalDateTime;
import java.util.List;

public interface IndustryDataAssignmentRepositoryCustom {

    LocalDateTime findLastModifiedAtByAssessmentRound(Long assessmentRoundId);

    List<AssignmentRow> findIndustryDataAssignment(Long assessmentRoundId, String q);
    List<AssignmentRow> findIndustryDataAssignmentByUser(Long assessmentRoundId, Long userId);
}
