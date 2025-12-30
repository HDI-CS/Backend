package kr.co.hdi.admin.evaluation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusByMemberResponse;
import kr.co.hdi.admin.evaluation.service.EvaluationService;
import kr.co.hdi.admin.evaluation.service.EvaluationServiceResolver;
import kr.co.hdi.admin.survey.service.SurveyService;
import kr.co.hdi.admin.survey.service.SurveyServiceResolver;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/{type}/evaluations")
@Tag(name = "평가 응답 ", description = "평가 응답 관리 API")
public class EvaluationController {
    private final EvaluationServiceResolver resolver;

    @GetMapping("/assessment/{assessmentRoundId}")
    public ResponseEntity<List<EvaluationStatusByMemberResponse>> getEvaluationStatus(
            @PathVariable("type")DomainType type,
            @PathVariable("assessmentRoundId") Long assessmentRoundId){

        EvaluationService evaluationService = resolver.resolve(type);
        List<EvaluationStatusByMemberResponse> responses =
                evaluationService.getEvaluationStatus(type, assessmentRoundId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
