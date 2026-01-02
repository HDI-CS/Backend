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
public class VisualWeightedScore extends BaseTimeEntityWithDeletion {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_weighted_score")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;

    @Enumerated(EnumType.STRING)
    private VisualDataCategory visualDataCategory;

    private Integer score1;   // 심미성

    private Integer score2;   // 조형성

    private Integer score3;   // 독창성

    private Integer score4;   // 사용성

    private Integer score5;   // 기능성

    private Integer score6;   // 윤리성

    private Integer score7;   // 경제성

    private Integer score8;   // 목적성
}
