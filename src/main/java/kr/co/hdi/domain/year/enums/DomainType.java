package kr.co.hdi.domain.year.enums;

import kr.co.hdi.domain.user.entity.UserType;

public enum DomainType {

    VISUAL,
    INDUSTRY;

    public UserType toUserType() {
        return switch (this) {
            case INDUSTRY -> UserType.INDUSTRY;
            case VISUAL -> UserType.VISUAL;
        };
    }
}
