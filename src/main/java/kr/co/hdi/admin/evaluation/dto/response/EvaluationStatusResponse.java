package kr.co.hdi.admin.evaluation.dto.response;

import kr.co.hdi.admin.evaluation.dto.enums.EvaluationStatus;
import kr.co.hdi.admin.evaluation.dto.enums.EvaluationType;

public record EvaluationStatusResponse(
        EvaluationType evalType,
        EvaluationStatus evalStatus
) {
    public static EvaluationStatusResponse of(EvaluationType type, boolean done) {
        return new EvaluationStatusResponse(type, done ? EvaluationStatus.DONE : EvaluationStatus.NOT_DONE);
    }

    public boolean isDone() {
        return evalStatus == EvaluationStatus.DONE;
    }
}
