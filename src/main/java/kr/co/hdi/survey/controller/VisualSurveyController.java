package kr.co.hdi.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.co.hdi.survey.dto.request.SurveyResponseRequest;
import kr.co.hdi.survey.dto.response.VisualSurveyDetailResponse;
import kr.co.hdi.survey.dto.response.SurveyDataPreviewResponse;
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
}
