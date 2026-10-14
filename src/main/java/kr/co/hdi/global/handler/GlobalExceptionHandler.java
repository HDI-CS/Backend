package kr.co.hdi.global.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import kr.co.hdi.global.dto.ResponseError;
import kr.co.hdi.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DB 제약조건 위반 (FK, UNIQUE 등) - 처리되지 않으면 보안 필터 체인에서 403으로 노출되는 문제가 있어 명시적으로 처리한다.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseError> handleDataIntegrityViolationException(DataIntegrityViolationException e,
                                                                                HttpServletRequest request) {
        log.error("[DataIntegrityViolationException] uri={}", request.getRequestURI(), e);

        ResponseError responseError = ResponseError.builder()
                .messageDetail("요청을 처리할 수 없습니다. 연관된 데이터가 존재하는지 확인해주세요.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseError);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseError> handleCustomException(CustomException e,
                                                               HttpServletRequest request) {

        ResponseError responseError = ResponseError.builder()
                .messageDetail(e.getMessage())
                .errorDetail(e.getErrorCode().getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(responseError);

    }

    // 요청 본문이 없거나 변환할 수 없는 경우 (NOT NULL)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                                                     HttpServletRequest request) {

        ResponseError responseError = ResponseError.builder()
                .messageDetail("요청 본문이 누락되었거나 올바르지 않습니다.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    // @Valid 어노테이션으로 DTO 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidationExceptions(MethodArgumentNotValidException e,
                                                                    HttpServletRequest request) {
        ResponseError responseError = ResponseError.builder()
                .messageDetail("유효성 검사 실패")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }


    // Request Param, Path Variable 등의 유효성 검사 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleConstraintViolationException(ConstraintViolationException e,
                                                                            HttpServletRequest request) {

        ResponseError responseError = ResponseError.builder()
                .messageDetail("파라미터 유효성 검사 실패")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler({HttpSessionRequiredException.class, MissingRequestCookieException.class})
    public ResponseEntity<ResponseError> handleSessionRequiredException(Exception e,
                                                                        HttpServletRequest request) {
        // 기존의 ResponseError DTO 형식에 맞춰 에러 응답을 생성합니다.
        ResponseError responseError = ResponseError.builder()
                .messageDetail("로그인이 필요한 서비스입니다.") // 클라이언트에게 보여줄 명확한 메시지
                .errorDetail("세션이 없거나 유효하지 않습니다.") // 개발자를 위한 구체적인 에러 내용
                .path(request.getRequestURI())
                .build();

        // 500 에러 대신, 의미에 맞는 401 Unauthorized 상태 코드를 반환합니다.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseError);
    }

    // 존재하지 않는 리소스에 대한 수정/삭제 시도 (예: findById 없이 바로 update/delete 쿼리를 날리는 경우)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ResponseError> handleEmptyResultDataAccessException(EmptyResultDataAccessException e,
                                                                               HttpServletRequest request) {
        ResponseError responseError = ResponseError.builder()
                .messageDetail("존재하지 않는 데이터입니다.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

    // 낙관적 락 충돌 (동시 수정)
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ResponseError> handleOptimisticLockingFailureException(OptimisticLockingFailureException e,
                                                                                  HttpServletRequest request) {
        log.warn("[OptimisticLockingFailureException] uri={}", request.getRequestURI(), e);

        ResponseError responseError = ResponseError.builder()
                .messageDetail("다른 요청에 의해 데이터가 변경되었습니다. 다시 시도해주세요.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseError);
    }

    // Path Variable / Query Param 타입 불일치 (예: Long 자리에 문자열)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
                                                                                    HttpServletRequest request) {
        ResponseError responseError = ResponseError.builder()
                .messageDetail("요청 파라미터 형식이 올바르지 않습니다.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    // 필수 Query Param 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseError> handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
                                                                                        HttpServletRequest request) {
        ResponseError responseError = ResponseError.builder()
                .messageDetail("필수 요청 파라미터가 누락되었습니다.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    // 지원하지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseError> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                                                       HttpServletRequest request) {
        ResponseError responseError = ResponseError.builder()
                .messageDetail("지원하지 않는 HTTP 메서드입니다.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(responseError);
    }

    // 존재하지 않는 경로 요청
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseError> handleNoResourceFoundException(NoResourceFoundException e,
                                                                         HttpServletRequest request) {
        ResponseError responseError = ResponseError.builder()
                .messageDetail("존재하지 않는 경로입니다.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

    // 그 외 분류되지 않은 모든 예외 - 보안 필터 체인 등에서 403으로 잘못 노출되는 것을 막기 위한 최종 안전망
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleException(Exception e,
                                                          HttpServletRequest request) {
        log.error("[Unhandled Exception] uri={}", request.getRequestURI(), e);

        ResponseError responseError = ResponseError.builder()
                .messageDetail("서버 내부 오류가 발생했습니다.")
                .errorDetail(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
    }

}