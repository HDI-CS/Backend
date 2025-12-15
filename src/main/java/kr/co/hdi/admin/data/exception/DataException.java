package kr.co.hdi.admin.data.exception;

import kr.co.hdi.global.exception.CustomException;

public class DataException extends CustomException {

    public DataException(DataErrorCode errorCode) {
        super(errorCode);
    }

    public DataException(DataErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
