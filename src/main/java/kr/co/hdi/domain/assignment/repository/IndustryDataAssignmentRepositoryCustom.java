package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;

import java.util.List;

public interface IndustryDataAssignmentRepositoryCustom {

    List<AssignmentRow> findIndustryDataAssignment(Long assessmentRoundId);
    List<AssignmentRow> findIndustryDataAssignmentByUser(Long assessmentRoundId, Long userId);
}
