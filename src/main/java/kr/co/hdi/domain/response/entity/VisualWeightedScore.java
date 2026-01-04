package kr.co.hdi.domain.response.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_year_round_id", "visual_data_category"}
        )
)
public class VisualWeightedScore extends BaseTimeEntityWithDeletion {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_weighted_score")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_year_round_id")
    private UserYearRound userYearRound;

    @Enumerated(EnumType.STRING)
    @Column(name = "visual_data_category")
    private VisualDataCategory visualDataCategory;

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

    public static VisualWeightedScore create(
            UserYearRound userYearRound,
            VisualDataCategory category
    ) {
        VisualWeightedScore v = new VisualWeightedScore();
        v.userYearRound = userYearRound;
        v.visualDataCategory = category;

        v.score1 = 0;
        v.score2 = 0;
        v.score3 = 0;
        v.score4 = 0;
        v.score5 = 0;
        v.score6 = 0;
        v.score7 = 0;
        v.score8 = 0;

        return v;
    }
}
