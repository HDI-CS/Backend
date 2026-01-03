package kr.co.hdi.survey.dto.response;

public record VisualSurveyDetailResponse(
        VisualDatasetResponse visualDatasetResponse,
        SurveyResponse brandSurveyResponse
) {
}
