package kr.co.hdi.admin.evaluation.dto.response;
import kr.co.hdi.domain.assignment.query.DataIdCodePair;
import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record EvaluationAnswerByMemberResponse(
        Long memberId,
        String memberName,
        List<EvaluationAnswerByDataResponse> surveyDatas
) {
    public static EvaluationAnswerByMemberResponse of(
            UserEntity user,
            List<EvaluationAnswerByDataResponse> surveyDatas
    ) {
        return new EvaluationAnswerByMemberResponse(
                user.getId(),
                user.getName(),
                surveyDatas
        );
    }
}
