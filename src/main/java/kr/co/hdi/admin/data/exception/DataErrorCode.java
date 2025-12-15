package kr.co.hdi.admin.data.exception;

import kr.co.hdi.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum DataErrorCode implements ErrorCode {

    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),
    YEAR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 연도를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
