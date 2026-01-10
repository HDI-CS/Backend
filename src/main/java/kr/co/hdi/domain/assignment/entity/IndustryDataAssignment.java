package kr.co.hdi.domain.assignment.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_userYearRound_industryData",
                columnNames = {"user_year_round_id", "industry_data_id"}
        )
)
public class IndustryDataAssignment extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_data_assignment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;

    @ManyToOne(fetch = FetchType.LAZY)
    private IndustryData industryData;

    private Integer surveyCount;   // 설문 문항 개수
    private Integer responseCount;   // 응답 개수

    public void incrementResponseCount() {
        if (this.responseCount == null) {
            this.responseCount = 1;
        } else {
            this.responseCount += 1;
        }
    }

    @Builder
    private IndustryDataAssignment(UserYearRound userYearRound, IndustryData industryData, Integer surveyCount) {
        this.userYearRound = userYearRound;
        this.industryData = industryData;
        this.surveyCount = surveyCount;
        this.responseCount = 0;
    }

    public static IndustryDataAssignment create(
            UserYearRound userYearRound,
            IndustryData industryData,
            Integer surveyCount
    ) {
        return IndustryDataAssignment.builder()
                .userYearRound(userYearRound)
                .industryData(industryData)
                .surveyCount(surveyCount)
                .build();
    }

    public static List<IndustryDataAssignment> createAll(
            UserYearRound userYearRound,
            List<IndustryData> industryDataList,
            Integer surveyCount
    ) {
        return industryDataList.stream()
                .map(industryData -> create(userYearRound, industryData, surveyCount))
                .toList();
    }
}
