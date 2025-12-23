package kr.co.hdi.domain.year.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class AssessmentRound extends BaseTimeEntityWithDeletion {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "assessment_round_id")
    private Long id;

    private Integer assessmentRound;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private DomainType domainType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;

}
