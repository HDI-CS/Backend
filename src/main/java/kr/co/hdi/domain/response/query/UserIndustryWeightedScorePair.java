package kr.co.hdi.domain.response.query;

import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.enums.VisualDataCategory;

public record UserIndustryWeightedScorePair(
        Long userId,
        String userName,
        Integer score1,
        Integer score2,
        Integer score3,
        Integer score4,
        Integer score5,
        Integer score6,
        Integer score7,
        Integer score8,
        IndustryDataCategory industryDataCategory
) {
}
