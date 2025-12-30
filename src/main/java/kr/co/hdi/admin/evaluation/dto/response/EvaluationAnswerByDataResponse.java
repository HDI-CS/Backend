package kr.co.hdi.admin.evaluation.dto.response;

import java.util.List;

public record EvaluationAnswerByDataResponse(
        Long dataId,
        String dataCode,
        List<EvaluationAnswerResponse> answers
) {
}
