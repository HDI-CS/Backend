package kr.co.hdi.domain.response.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class VisualResponse extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_response_id")
    private Long id;

    private Integer numberResponse;  // 정량

    @Column(columnDefinition = "text")
    private String textResponse;  // 정성

    @ManyToOne(fetch = FetchType.LAZY)
    private VisualData visualData;

    @ManyToOne(fetch = FetchType.LAZY)
    private VisualSurvey visualSurvey;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;
}
