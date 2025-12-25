package kr.co.hdi.admin.survey.dto.response;

import kr.co.hdi.domain.year.entity.AssessmentRound;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SurveyRoundResponse (
        Long roundId,
        String folderName,
        LocalDateTime updatedAt,
        LocalDateTime createdAt,
        LocalDate startDate,
        LocalDate endDate
){
    public static SurveyRoundResponse from(AssessmentRound r) {
        return new SurveyRoundResponse(
                r.getId(),
                r.getAssessmentRound(),
                r.getUpdatedAt(),
                r.getCreatedAt(),
                r.getStartDate(),
                r.getEndDate()
        );
    }
}
