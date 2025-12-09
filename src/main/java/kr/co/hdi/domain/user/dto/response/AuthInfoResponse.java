package kr.co.hdi.domain.user.dto.response;

import kr.co.hdi.domain.user.entity.UserType;

public record AuthInfoResponse(
        UserType userType,
        Boolean surveyDone
) {
}
