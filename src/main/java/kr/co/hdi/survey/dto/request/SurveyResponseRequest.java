package kr.co.hdi.survey.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SurveyResponseRequest(
        Long surveyId,

        // 정량 평가
        @Schema(description = "정량 평가 (점수 1,2,3,4,5)")
        Integer response,

        // 정성 평가
        @Schema(description = "정성 평가 (서술형 답변)")
        String textResponse
) {
}
