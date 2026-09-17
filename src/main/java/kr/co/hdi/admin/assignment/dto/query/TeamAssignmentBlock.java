package kr.co.hdi.admin.assignment.dto.query;

import java.util.List;

/*
엑셀 매칭 업로드 양식(팀당 5컬럼: 팀명 | connect_id | 값 | pw | 값, 그 아래 아이디/부문/주체/분류 표)에서
팀 하나를 파싱한 결과.
한 팀에 전문가가 최대 2명(팀 헤더 행 + 바로 아래 행)까지 들어갈 수 있다.
 */
public record TeamAssignmentBlock(
        String team,                        // 예: "팀A"
        List<ExpertCredential> experts,      // 팀에 매칭된 전문가(최대 2명)
        List<String> dataCodes              // 매칭된 데이터의 "아이디" (brandCode / originalId) 목록
) {
    public boolean isEmpty() {
        return experts.isEmpty() && dataCodes.isEmpty();
    }

    public record ExpertCredential(
            String connectId, // 로그인 이메일
            String password   // 평문 비밀번호 (검증용, 참고 표시만 함)
    ) {
    }
}
