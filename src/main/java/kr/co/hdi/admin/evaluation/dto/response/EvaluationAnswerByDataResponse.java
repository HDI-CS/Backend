package kr.co.hdi.admin.evaluation.dto.response;
import kr.co.hdi.domain.assignment.query.DataIdCodePair;
import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record EvaluationAnswerByDataResponse(
        Long dataId,
        String dataCode,
        List<EvaluationAnswerResponse> surveyss
) {
    public static EvaluationAnswerByDataResponse of(
            DataIdCodePair pair,
            List<IndustrySurvey> surveys,
            List<IndustryResponse> responsesForData
    ) {
        Map<Long, IndustryResponse> responseBySurveyId = responsesForData.stream()
                .collect(Collectors.toMap(
                        r -> r.getIndustrySurvey().getId(),
                        r -> r
                ));

        List<EvaluationAnswerResponse> answers = surveys.stream()
                .sorted(Comparator.comparing(IndustrySurvey::getSurveyNumber))
                .map(s -> EvaluationAnswerResponse.fromSurveyAndResponse(
                        s,
                        responseBySurveyId.get(s.getId())
                ))
                .toList();

        return new EvaluationAnswerByDataResponse(pair.dataId(), pair.dataCode(), answers);
    }
}
