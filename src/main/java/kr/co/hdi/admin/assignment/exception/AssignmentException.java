package kr.co.hdi.admin.assignment.exception;

import kr.co.hdi.global.exception.CustomException;

public class AssignmentException extends CustomException {

    public AssignmentException(AssignmentErrorCode errorCode) {
        super(errorCode);
    }

    public AssignmentException(AssignmentErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
