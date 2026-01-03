package kr.co.hdi.domain.assignment.entity;

import jakarta.persistence.*;
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
public class VisualDataAssignment extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_data_assignment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserYearRound userYearRound;

    @ManyToOne(fetch = FetchType.LAZY)
    private VisualData visualData;

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
    private VisualDataAssignment(UserYearRound userYearRound, VisualData visualData) {
        this.userYearRound = userYearRound;
        this.visualData = visualData;
        this.surveyCount = 0;
        this.responseCount = 0;
    }

    public static VisualDataAssignment create(
            UserYearRound userYearRound,
            VisualData visualData
    ) {
        return VisualDataAssignment.builder()
                .userYearRound(userYearRound)
                .visualData(visualData)
                .build();
    }

    public static List<VisualDataAssignment> createAll(
            UserYearRound userYearRound,
            List<VisualData> visualDataList
    ) {
        return visualDataList.stream()
                .map(visualData -> create(userYearRound, visualData))
                .toList();
    }
}
