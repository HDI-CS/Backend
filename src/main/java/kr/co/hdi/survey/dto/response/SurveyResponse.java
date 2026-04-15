package kr.co.hdi.survey.dto.response;

import java.util.List;

public record SurveyResponse(
        String dataCode,
        boolean isSubmitted,
        List<NumberSurveyResponse> response,
        TextSurveyResponse textResponse
) {
}
