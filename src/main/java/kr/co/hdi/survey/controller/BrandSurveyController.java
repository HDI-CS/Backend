package kr.co.hdi.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.co.hdi.survey.dto.request.SurveyResponseRequest;
import kr.co.hdi.survey.dto.request.WeightedScoreRequest;
import kr.co.hdi.survey.dto.response.BrandSurveyDetailResponse;
import kr.co.hdi.survey.dto.response.ProductSurveyDataResponse;
import kr.co.hdi.survey.dto.response.WeightedScoreResponse;
import kr.co.hdi.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
public class BrandSurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "유저에게 할당된 브랜드 설문 목록 조회")
    @GetMapping("/visual")
    public ResponseEntity<List<ProductSurveyDataResponse>> getVisualSurveys(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {
        List<ProductSurveyDataResponse> response = surveyService.getAllVisualSurveys(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "브랜드 설문 상세 조회 (설문하러가기 누를때)")
    @GetMapping("/visual/{dataId}")
    public ResponseEntity<BrandSurveyDetailResponse> getVisualSurveyDetail(
            @PathVariable Long dataId,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        BrandSurveyDetailResponse response = surveyService.getVisualSurveyDetail(dataId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "브랜드 설문 응답 한개 저장")
    @PostMapping("/brand/{brandResponseId}")
    public ResponseEntity<Void> saveBrandSurveyResponse(
            @PathVariable Long brandResponseId,
            @RequestBody SurveyResponseRequest request) {

        surveyService.saveBrandSurveyResponse(brandResponseId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "제출")
    @PostMapping("/brand/{brandResponseId}/submit")
    public ResponseEntity<Void> submitBrandSurvey(
            @PathVariable Long brandResponseId,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {
        surveyService.setBrandResponseStatusDone(brandResponseId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "가중치 평가")
    @PatchMapping("/scores/weighted")
    public ResponseEntity<Void> saveWeightedScores(
            @RequestBody List<WeightedScoreRequest> requests,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        surveyService.saveWeightedScores(userId, requests);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scores/weighted")
    public ResponseEntity<List<WeightedScoreResponse>> getWeightesScores(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        List<WeightedScoreResponse> response = surveyService.getWeightedResponse(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
