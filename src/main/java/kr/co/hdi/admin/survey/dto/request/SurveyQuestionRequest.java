package kr.co.hdi.admin.survey.dto.request;

import kr.co.hdi.domain.survey.enums.SurveyType;

public record SurveyQuestionRequest(
        SurveyType type,
        Integer surveyNumber,
        String surveyCode,
        String surveyContent
) {
}
