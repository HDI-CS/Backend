package kr.co.hdi.survey.dto.request.visual;

import kr.co.hdi.domain.data.enums.VisualDataCategory;

public record VisualWeightedScoreRequest(
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
}
