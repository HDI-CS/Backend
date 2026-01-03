package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.response.entity.VisualResponse;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.entity.VisualSurvey;

public record TextSurveyResponse(
        Long surveyId,
        String survey,
        String sampleText,
        String response
) {

    public static TextSurveyResponse of(IndustrySurvey survey, IndustryResponse response) {

        return new TextSurveyResponse(
                survey.getId(),
                survey.getSurveyContent(),
                "",
                response != null ? response.getTextResponse() : null
        );
    }

    public static TextSurveyResponse of(VisualSurvey survey, VisualResponse response) {

        return new TextSurveyResponse(
                survey.getId(),
                survey.getSurveyContent(),
                "",
                response != null ? response.getTextResponse() : null
        );
    }
}
