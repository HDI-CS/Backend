package kr.co.hdi.admin.evaluation.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationAnswerByMemberResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusByMemberResponse;
import kr.co.hdi.admin.evaluation.service.EvaluationService;
import kr.co.hdi.admin.evaluation.service.EvaluationServiceResolver;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/assessment/{assessmentRoundId}/datasets/export")
    @Operation(summary = "평가 응답 데이터셋 엑셀 다운로드")
    public ResponseEntity<Resource> exportEvaluationData(
            @PathVariable("type") DomainType type,
            @PathVariable("assessmentRoundId") Long assessmentRoundId
    ) {
        EvaluationService evaluationService = resolver.resolve(type);
        byte[] bytes = evaluationService.exportEvaluationExcelsZip(type, assessmentRoundId);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = type + "_evaluation_responses_" + assessmentRoundId + ".xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .contentLength(bytes.length)
                .body(resource);
    }
}
