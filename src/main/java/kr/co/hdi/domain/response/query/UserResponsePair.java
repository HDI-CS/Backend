package kr.co.hdi.domain.response.query;

public record UserResponsePair(
        Long userId,
        Long dataId,
        Integer numberResponse,
        String textResponse
) {
}
