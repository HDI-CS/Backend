package kr.co.hdi.admin.evaluation.dto.response;
import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.enums.SurveyType;

public record EvaluationAnswerResponse(
        Long surveyId,
        SurveyType surveyType,
        Integer surveyNumber,
        String surveyContent,
        String answerContent
) {
    public static EvaluationAnswerResponse of(
            Long surveyId,
            SurveyType type,
            Integer surveyNumber,
            String surveyContent,
            String answerContent
    ) {
        return new EvaluationAnswerResponse(
                surveyId, type, surveyNumber, surveyContent, answerContent
        );
    }

    public static EvaluationAnswerResponse unanswered(
            Long surveyId,
            SurveyType type,
            Integer surveyNumber,
            String surveyContent
    ) {
        return of(surveyId, type, surveyNumber, surveyContent, null);
    }

    public static EvaluationAnswerResponse fromSurvey(IndustrySurvey s) {
        return unanswered(
                s.getId(),
                s.getSurveyType(),
                s.getSurveyNumber(),
                s.getSurveyContent()
        );
    }

    public static EvaluationAnswerResponse fromSurveyAndResponse(
            IndustrySurvey s,
            IndustryResponse r
    ) {
        String answerContent = null;

        if (r != null) {
            if (s.getSurveyType() == SurveyType.NUMBER) {
                Integer num = r.getNumberResponse();
                answerContent = (num == null) ? null : String.valueOf(num);
            } else if (s.getSurveyType() == SurveyType.TEXT) {
                String txt = r.getTextResponse();
                answerContent = (txt == null || txt.isBlank()) ? null : txt;
            }
        }

        return of(
                s.getId(),
                s.getSurveyType(),
                s.getSurveyNumber(),
                s.getSurveyContent(),
                answerContent
        );
    }
}
