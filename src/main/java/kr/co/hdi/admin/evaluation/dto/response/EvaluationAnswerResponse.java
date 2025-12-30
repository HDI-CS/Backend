package kr.co.hdi.admin.evaluation.dto.response;

import kr.co.hdi.domain.survey.enums.SurveyType;

public record EvaluationAnswerResponse(
        SurveyType answerType,
        String answerContent
) {
}
