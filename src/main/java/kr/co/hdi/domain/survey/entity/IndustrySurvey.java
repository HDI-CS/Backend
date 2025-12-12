package kr.co.hdi.domain.survey.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.survey.enums.SurveyType;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
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

    @Enumerated(EnumType.STRING)
    private SurveyType surveyType;

    @ManyToOne(fetch = FetchType.LAZY)
    private AssessmentRound assessmentRound;
}
