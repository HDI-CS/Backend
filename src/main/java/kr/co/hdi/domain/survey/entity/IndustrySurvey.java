package kr.co.hdi.domain.survey.entity;

import jakarta.persistence.*;
import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
import kr.co.hdi.admin.survey.dto.response.SurveyQuestionResponse;
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
public class IndustrySurvey extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_survey_id")
    private Long id;

    private Integer surveyNumber;

    private String surveyCode;

    private String surveyContent;

    private String sampleText;

    @Enumerated(EnumType.STRING)
    private SurveyType surveyType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;

    @Builder
    private IndustrySurvey(
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

    public static IndustrySurvey create(
            SurveyQuestionRequest req,
            Year year) {
        return IndustrySurvey.builder()
                .surveyNumber(req.surveyNumber())
                .surveyCode(req.surveyCode())
                .surveyContent(req.surveyContent())
                .surveyType(req.type())
                .year(year)
                .build();
    }

    public void updateSampleText(String s) {
        this.sampleText = s;
    }
}
