package kr.co.hdi.admin.evaluation.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationAnswerByMemberResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusByMemberResponse;
import kr.co.hdi.admin.evaluation.service.EvaluationService;
import kr.co.hdi.admin.evaluation.service.EvaluationServiceResolver;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/{type}/evaluations")
@Tag(name = "평가 응답 ", description = "평가 응답 관리 API")
public class EvaluationController {
    private final EvaluationServiceResolver resolver;

    @GetMapping("/assessment/{assessmentRoundId}/search")
    @Operation(summary = "평가 응답 전체 조회")
    public ResponseEntity<List<EvaluationStatusByMemberResponse>> getEvaluationStatus(
            @PathVariable("type")DomainType type,
            @PathVariable("assessmentRoundId") Long assessmentRoundId,
            @RequestParam(defaultValue = "") String q){

        EvaluationService evaluationService = resolver.resolve(type);
        List<EvaluationStatusByMemberResponse> responses =
                evaluationService.getEvaluationStatus(type, assessmentRoundId, q);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/assessment/{assessmentRoundId}/members/{memberId}")
    @Operation(summary = "특정 전문가 응답 전체 조회")
    public ResponseEntity<EvaluationAnswerByMemberResponse> getEvaluationByMember(
            @PathVariable("type") DomainType type,
            @PathVariable("assessmentRoundId") Long assessmentRoundId,
            @PathVariable("memberId") Long memberId
    ){
        EvaluationService evaluationService = resolver.resolve(type);
        EvaluationAnswerByMemberResponse response =
                evaluationService.getEvaluationByMember(type, assessmentRoundId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
