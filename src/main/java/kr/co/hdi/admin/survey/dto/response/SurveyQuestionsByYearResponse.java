package kr.co.hdi.admin.survey.dto.response;

import java.util.List;

public record SurveyQuestionsByYearResponse(
        String folderName,
        List<SurveyQuestionTypeResponse> surveyQuestions
) {
}
