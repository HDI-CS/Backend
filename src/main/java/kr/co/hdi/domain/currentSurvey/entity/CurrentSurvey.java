package kr.co.hdi.domain.currentSurvey.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentSurvey {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "current_survey_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private DomainType domainType;

    private Long yearId;

    private Long assessmentRoundId;

    @Builder
    public CurrentSurvey(DomainType domainType, Long yearId, Long assessmentRoundId) {
        this.domainType = domainType;
        this.yearId = yearId;
        this.assessmentRoundId = assessmentRoundId;
    }
}
