package kr.co.hdi.admin.data.exception;

import kr.co.hdi.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum DataErrorCode implements ErrorCode {

    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),
    YEAR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 연도를 찾을 수 없습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "해당 연도에 허용되지 않는 카테고리입니다"),
    DUPLICATE_FIELD_SPEC(HttpStatus.CONFLICT, "이미 같은 키로 정의된 스펙이 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
