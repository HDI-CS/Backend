package kr.co.hdi.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.survey.dto.request.SurveyResponseRequest;
import kr.co.hdi.survey.dto.request.industry.IndustryWeightedScoreRequest;
import kr.co.hdi.survey.dto.response.SurveyDataPreviewResponse;
import kr.co.hdi.survey.dto.response.IndustrySurveyDetailResponse;
import kr.co.hdi.survey.dto.response.industry.IndustryWeightedScoreResponse;
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
@RequestMapping("/api/v1/user/industry/survey")
@Tag(name = "산업 디자인 평가 페이지", description = "산업 디자인 평가 페이지 API")
public class IndustrySurveyController {

    private final SurveyService surveyService;

    @GetMapping
    @Operation(summary = "유저에게 할당된 제품 설문 목록 조회")
    public ResponseEntity<List<SurveyDataPreviewResponse>> getSurveys(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {
        log.debug("Session userId: {}", userId);

        List<SurveyDataPreviewResponse> response = surveyService.getAllIndustrySurveys(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{dataId}")
    @Operation(summary = "제품 설문 상세 조회 (설문하러가기 누를때)")
    public ResponseEntity<IndustrySurveyDetailResponse> getProductSurveyDetail(
            @PathVariable Long dataId,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {
        IndustrySurveyDetailResponse response = surveyService.getIndustrySurveyDetail(dataId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{dataId}")
    @Operation(summary = "제품 설문 응답 한개 저장")
    public ResponseEntity<Void> saveProductSurveyResponse(
            @PathVariable Long dataId,
            @RequestBody SurveyResponseRequest request,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {
        surveyService.saveIndustrySurveyResponse(dataId, userId, request);
        return ResponseEntity.ok().build();
    }

//    @Operation(summary = "제출")
//    @PostMapping("/product/{productResponseId}/submit")
//    public ResponseEntity<Void> submitProductSurvey(
//            @PathVariable Long productResponseId,
//            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
//    ) {
//        surveyService.setProductResponseStatusDone(productResponseId, userId);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/weighted-score")
    @Operation(summary = "산업 디자인 가중치 응답 조회")
    public ResponseEntity<List<IndustryWeightedScoreResponse>> getWeightedScore(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        List<IndustryWeightedScoreResponse> responses = surveyService.getIndustryWeightedResponse(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/weighted-score")
    @Operation(summary = "산업 디자인 가중치 응답 저장")
    public ResponseEntity<Void> saveWeightedScore(
            @RequestBody IndustryWeightedScoreRequest request,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        surveyService.saveIndustryWeightedResponse(request);
        return ResponseEntity.ok().build();
    }
}
