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
public class IndustryDataAssignment extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_data_assignment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;

    @ManyToOne(fetch = FetchType.LAZY)
    private IndustryData industryData;

    @Builder
    private IndustryDataAssignment(UserYearRound userYearRound, IndustryData industryData) {
        this.userYearRound = userYearRound;
        this.industryData = industryData;
    }

    public static IndustryDataAssignment create(
            UserYearRound userYearRound,
            IndustryData industryData
    ) {
        return IndustryDataAssignment.builder()
                .userYearRound(userYearRound)
                .industryData(industryData)
                .build();
    }

    public static List<IndustryDataAssignment> createAll(
            UserYearRound userYearRound,
            List<IndustryData> industryDataList
    ) {
        return industryDataList.stream()
                .map(industryData -> create(userYearRound, industryData))
                .toList();
    }
}
