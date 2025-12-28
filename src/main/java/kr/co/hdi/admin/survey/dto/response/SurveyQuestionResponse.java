package kr.co.hdi.admin.survey.dto.response;

import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.entity.VisualSurvey;

public record SurveyQuestionResponse(
        Long id,
        Integer surveyNumber,
        String surveyCode,
        String surveyContent
) {
    public static SurveyQuestionResponse from(IndustrySurvey s) {
        return new SurveyQuestionResponse(
                s.getId(),
                s.getSurveyNumber(),
                s.getSurveyCode(),
                s.getSurveyContent()
        );
    }

    public static SurveyQuestionResponse from(VisualSurvey s) {
        return new SurveyQuestionResponse(
                s.getId(),
                s.getSurveyNumber(),
                s.getSurveyCode(),
                s.getSurveyContent()
        );
    }
}
