package kr.co.hdi.domain.response.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class IndustryWeightedScore extends BaseTimeEntityWithDeletion {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_weighted_score")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;

    @Enumerated(EnumType.STRING)
    private IndustryDataCategory industryDataCategory;

    private Integer score1;   // 심미성

    private Integer score2;   // 조형성

    private Integer score3;   // 독창성

    private Integer score4;   // 사용성

    private Integer score5;   // 기능성

    private Integer score6;   // 윤리성

    private Integer score7;   // 경제성

    private Integer score8;   // 목적성

    public void updateScore(
            Integer score1,
            Integer score2,
            Integer score3,
            Integer score4,
            Integer score5,
            Integer score6,
            Integer score7,
            Integer score8) {
        this.score1 = score1;
        this.score2 = score2;
        this.score3 = score3;
        this.score4 = score4;
        this.score5 = score5;
        this.score6 = score6;
        this.score7 = score7;
        this.score8 = score8;
    }
}
