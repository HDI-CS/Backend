package kr.co.hdi.admin.survey.dto.response;

import kr.co.hdi.domain.survey.enums.SurveyType;

import java.util.List;

public record SurveyQuestionTypeResponse(
        SurveyType type,
        List<SurveyQuestionResponse> questions
) {
}
