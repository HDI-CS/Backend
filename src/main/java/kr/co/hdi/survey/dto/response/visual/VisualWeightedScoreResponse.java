package kr.co.hdi.survey.dto.response.visual;

import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.response.entity.VisualWeightedScore;

public record VisualWeightedScoreResponse(
        Long id,
        VisualDataCategory category,
        int score1,
        int score2,
        int score3,
        int score4,
        int score5,
        int score6,
        int score7,
        int score8
) {

    public static VisualWeightedScoreResponse fromEntity(VisualWeightedScore visualWeightedScore) {

        return new VisualWeightedScoreResponse(
                visualWeightedScore.getId(),
                visualWeightedScore.getVisualDataCategory(),
                visualWeightedScore.getScore1(),
                visualWeightedScore.getScore2(),
                visualWeightedScore.getScore3(),
                visualWeightedScore.getScore4(),
                visualWeightedScore.getScore5(),
                visualWeightedScore.getScore6(),
                visualWeightedScore.getScore7(),
                visualWeightedScore.getScore8()
        );
    }
}
