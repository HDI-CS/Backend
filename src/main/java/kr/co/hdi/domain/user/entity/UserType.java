package kr.co.hdi.domain.user.entity;

import kr.co.hdi.domain.year.enums.DomainType;

public enum UserType {

    BRAND, PRODUCT,
    VISUAL, INDUSTRY;

    public DomainType toDomainType() {
        return switch (this) {
            case BRAND -> DomainType.VISUAL;
            case PRODUCT -> DomainType.INDUSTRY;
            case VISUAL -> DomainType.VISUAL;
            case INDUSTRY -> DomainType.INDUSTRY;
        };
    }
}
