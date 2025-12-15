package kr.co.hdi.global.dto;

import lombok.Builder;

public record CommonResponse<T>(
        int code,
        String message,
        T result
) {

    @Builder
    public CommonResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

}
