package kr.co.hdi.admin.evaluation.exeption;

import kr.co.hdi.global.exception.CustomException;

public class EvaluationException extends CustomException {

    public EvaluationException(EvaluationErrorCode errorCode) {
        super(errorCode);
    }

    public EvaluationException(EvaluationErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
