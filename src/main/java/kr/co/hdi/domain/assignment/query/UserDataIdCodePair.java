package kr.co.hdi.domain.assignment.query;

public record UserDataIdCodePair(
        Long userId,
        String userName,
        Long dataId,
        String dataCode
) {
}
