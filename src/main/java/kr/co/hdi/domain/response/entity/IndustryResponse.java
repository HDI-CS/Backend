package kr.co.hdi.domain.response.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class IndustryResponse extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_response_id")
    private Long id;

    private Integer numberResponse;  // 정량

    @Column(columnDefinition = "text")
    private String textResponse;  // 정성

    @ManyToOne(fetch = FetchType.LAZY)
    private IndustryData industryData;

    @ManyToOne(fetch = FetchType.LAZY)
    private IndustrySurvey industrySurvey;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;
}
