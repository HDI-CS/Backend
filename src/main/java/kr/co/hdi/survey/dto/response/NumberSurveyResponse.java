package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.response.entity.VisualResponse;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.entity.VisualSurvey;

public record NumberSurveyResponse(
        Long surveyId,
        String survey,   // 설문 문항 내용
        Integer response  // 정량 평가 응답
) {

    public static NumberSurveyResponse of(IndustrySurvey survey, IndustryResponse response) {

        return new NumberSurveyResponse(
                survey.getId(),
                survey.getSurveyContent(),
                response != null ? response.getNumberResponse() : null
        );
    }

    public static NumberSurveyResponse of(VisualSurvey survey, VisualResponse response) {

        return new NumberSurveyResponse(
                survey.getId(),
                survey.getSurveyContent(),
                response != null ? response.getNumberResponse() : null
        );
    }
}
