package kr.co.hdi.domain.response.query;

public record UserSurveyResponsePair(
        Long userId,
        Long dataId,
        Integer surveyNumber,
        Integer numberResponse,
        String textResponse
) {
}
