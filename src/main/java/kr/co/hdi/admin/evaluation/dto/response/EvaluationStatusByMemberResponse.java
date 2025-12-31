package kr.co.hdi.admin.evaluation.dto.response;
import kr.co.hdi.domain.user.entity.UserEntity;
import java.util.List;

public record EvaluationStatusByMemberResponse(
        Long memberId,
        String memberName,
        Integer totalCount,
        Integer evaluatedCount,
        List<EvaluationStatusResponse> evalStatuses
) {
    public static EvaluationStatusByMemberResponse of(
            UserEntity user,
            List<EvaluationStatusResponse> statuses
    ) {
        Integer doneCount = (int) statuses.stream()
                .filter(s -> Boolean.TRUE.equals(s.isDone()))
                .count();

        return new EvaluationStatusByMemberResponse(
                user.getId(),
                user.getName(),
                statuses.size(),
                doneCount,
                statuses
        );
    }
}