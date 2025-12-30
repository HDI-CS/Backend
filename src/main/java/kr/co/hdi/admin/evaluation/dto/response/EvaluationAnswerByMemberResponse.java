package kr.co.hdi.admin.evaluation.dto.response;

import java.util.List;

public record EvaluationAnswerByMemberResponse(
        Long memberId,
        String memberName,
        List<EvaluationAnswerByDataResponse> surveyDatas
) {
}
