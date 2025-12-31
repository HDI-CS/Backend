package kr.co.hdi.admin.evaluation.dto.response;
import kr.co.hdi.domain.user.entity.UserEntity;
import java.util.List;

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
