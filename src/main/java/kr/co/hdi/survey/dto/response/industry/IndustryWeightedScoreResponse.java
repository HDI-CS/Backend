package kr.co.hdi.survey.dto.response.industry;

import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.response.entity.IndustryWeightedScore;

public record IndustryWeightedScoreResponse(
        Long id,
        IndustryDataCategory category,
        int score1,
        int score2,
        int score3,
        int score4,
        int score5,
        int score6,
        int score7,
        int score8
) {
    public static IndustryWeightedScoreResponse fromEntity(IndustryWeightedScore industryWeightedScore) {

        return new IndustryWeightedScoreResponse(
                industryWeightedScore.getId(),
                industryWeightedScore.getIndustryDataCategory(),
                industryWeightedScore.getScore1(),
                industryWeightedScore.getScore2(),
                industryWeightedScore.getScore3(),
                industryWeightedScore.getScore4(),
                industryWeightedScore.getScore5(),
                industryWeightedScore.getScore6(),
                industryWeightedScore.getScore7(),
                industryWeightedScore.getScore8()
        );
    }
}
