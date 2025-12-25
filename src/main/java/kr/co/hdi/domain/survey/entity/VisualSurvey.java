package kr.co.hdi.domain.survey.entity;

import jakarta.persistence.*;
import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
import kr.co.hdi.domain.survey.enums.SurveyType;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class VisualSurvey extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_survey_id")
    private Long id;

    private Integer surveyNumber;

    private String surveyCode;

    private String surveyContent;

    @Enumerated(EnumType.STRING)
    private SurveyType surveyType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;

    @Builder
    private VisualSurvey(
            Integer surveyNumber,
            String surveyCode,
            String surveyContent,
            SurveyType surveyType,
            Year year
    ) {
        this.surveyNumber = surveyNumber;
        this.surveyCode = surveyCode;
        this.surveyContent = surveyContent;
        this.surveyType = surveyType;
        this.year = year;
    }

    public void updateSurvey(String surveyContent) {
        this.surveyContent = surveyContent;
    }

    public static VisualSurvey create(
            SurveyQuestionRequest req,
            Year year) {
        return VisualSurvey.builder()
                .surveyNumber(req.surveyNumber())
                .surveyCode(req.surveyCode())
                .surveyContent(req.surveyContent())
                .surveyType(req.type())
                .year(year)
                .build();
    }
}
