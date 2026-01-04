package kr.co.hdi.domain.response.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Builder;
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

    public void updateNumberResponse(Integer numberResponse) {
        this.numberResponse = numberResponse;
    }

    public void updateTextResponse(String textResponse) {
        this.textResponse = textResponse;
    }

    @Builder
    public VisualResponse(Integer numberResponse, String textResponse, VisualData visualData, VisualSurvey visualSurvey, UserYearRound userYearRound) {
        this.numberResponse = numberResponse;
        this.textResponse = textResponse;
        this.visualData = visualData;
        this.visualSurvey = visualSurvey;
        this.userYearRound = userYearRound;
    }
}
