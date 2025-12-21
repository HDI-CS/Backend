package kr.co.hdi.domain.assignment.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class IndustryDataAssignment extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_data_assignment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;

    @ManyToOne(fetch = FetchType.LAZY)
    private IndustryData industryData;
}
