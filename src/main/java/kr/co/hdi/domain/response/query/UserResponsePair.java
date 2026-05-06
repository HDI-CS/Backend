package kr.co.hdi.domain.response.query;

public record UserResponsePair(
        Long userId,
        Long dataId,
        String surveyCode,
        Integer numberResponse,
        String textResponse
) {
}
