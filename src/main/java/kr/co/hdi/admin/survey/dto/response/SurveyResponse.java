package kr.co.hdi.admin.survey.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record SurveyResponse(
        Long yearId,
        String folderName,
        LocalDateTime updatedAt,
        LocalDateTime createdAt,
        List<SurveyRoundResponse> rounds
) {
}
