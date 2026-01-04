package kr.co.hdi.survey.dto.response;

import java.util.List;

public record SurveyResponse(
        String dataCode,
        List<NumberSurveyResponse> response,
        List<TextSurveyResponse> textResponse
) {
}
