package kr.co.hdi.survey.dto.response;

public record IndustrySurveyDetailResponse(
        IndustryDataSetResponse industryDataSetResponse,
        SurveyResponse productSurveyResponse
) {
}
