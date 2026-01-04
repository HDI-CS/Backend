package kr.co.hdi.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.survey.dto.request.SurveyResponseRequest;
import kr.co.hdi.survey.dto.request.visual.VisualWeightedScoreRequest;
import kr.co.hdi.survey.dto.response.VisualSurveyDetailResponse;
import kr.co.hdi.survey.dto.response.SurveyDataPreviewResponse;
import kr.co.hdi.survey.dto.response.visual.VisualWeightedScoreResponse;
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
@RequestMapping("/api/v1/user/visual/survey")
@Tag(name = "시각 디자인 평가 페이지", description = "시각 디자인 평가 페이지 API")
public class VisualSurveyController {

    private final SurveyService surveyService;

    @GetMapping
    @Operation(summary = "유저에게 할당된 브랜드 설문 목록 조회")
    public ResponseEntity<List<SurveyDataPreviewResponse>> getVisualSurveys(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {
        List<SurveyDataPreviewResponse> response = surveyService.getAllVisualSurveys(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{dataId}")
    @Operation(summary = "브랜드 설문 상세 조회 (설문하러가기 누를때)")
    public ResponseEntity<VisualSurveyDetailResponse> getVisualSurveyDetail(
            @PathVariable Long dataId,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        VisualSurveyDetailResponse response = surveyService.getVisualSurveyDetail(dataId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{dataId}")
    @Operation(summary = "브랜드 설문 응답 한개 저장")
    public ResponseEntity<Void> saveBrandSurveyResponse(
            @PathVariable Long dataId,
            @RequestBody SurveyResponseRequest request,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId) {

        surveyService.saveVisualSurveyResponse(dataId, userId, request);
        return ResponseEntity.ok().build();
    }

//    @Operation(summary = "제출")
//    @PostMapping("/brand/{brandResponseId}/submit")
//    public ResponseEntity<Void> submitBrandSurvey(
//            @PathVariable Long brandResponseId,
//            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
//    ) {
//        surveyService.setBrandResponseStatusDone(brandResponseId, userId);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/weighted-score")
    @Operation(summary = "시각 디자인 가중치 응답 조회")
    public ResponseEntity<List<VisualWeightedScoreResponse>> getWeightedScore(
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        List<VisualWeightedScoreResponse> responses = surveyService.getVisualWeightedResponse(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping("/weighted-score")
    @Operation(summary = "시각 디자인 가중치 응답 저장")
    public ResponseEntity<Void> saveWeightedScore(
            @RequestBody VisualWeightedScoreRequest request,
            @Parameter(hidden = true) @SessionAttribute(name = "userId", required = true) Long userId
    ) {

        surveyService.saveVisualWeightedResponse(request);
        return ResponseEntity.ok().build();
    }
}
