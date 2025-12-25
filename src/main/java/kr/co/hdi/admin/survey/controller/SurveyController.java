package kr.co.hdi.admin.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
import kr.co.hdi.admin.survey.dto.response.SurveyQuestionsByYearResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyRoundIdResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyYearIdResponse;
import kr.co.hdi.admin.survey.service.SurveyService;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.admin.survey.dto.response.SurveyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/{type}/survey")
@Tag(name = "평가 관리", description = "평가 관리 API")
public class SurveyController {

    private final SurveyService surveyService;

    // GET Method
    @GetMapping("/all")
    @Operation(summary = "전체 평가 조회")
    public ResponseEntity<List<SurveyResponse>> getSurveys(
            @PathVariable DomainType type){
        List<SurveyResponse> responses = surveyService.getSurveys(type);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/years/{yearId}/questions")
    @Operation(summary = "년도 평가 설문 문항 조회")
    public ResponseEntity<SurveyQuestionsByYearResponse> getSurveyQuestions(
            @PathVariable DomainType type,
            @PathVariable Long yearId){
        SurveyQuestionsByYearResponse response = surveyService.getSurveyQuestions(type, yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // POST Method
    @PostMapping
    @Operation(summary = "년도 평가 생성")
    public ResponseEntity<SurveyYearIdResponse> createSurvey(
            @PathVariable DomainType type
    ){
        SurveyYearIdResponse response = surveyService.createSurvey(type);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/years/{yearId}/questions")
    @Operation(summary = "년도 평가 설문 문항 생성")
    public ResponseEntity<?> createSurveyQuestion(
            @PathVariable DomainType type,
            @PathVariable Long yearId,
            @RequestBody List<SurveyQuestionRequest> request
    ){
        surveyService.createSurveyQuestion(type, yearId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/years/{yearId}/assessment")
    @Operation(summary = "차수 평가 생성")
    public ResponseEntity<SurveyRoundIdResponse> createRound(
            @PathVariable DomainType type,
            @PathVariable Long yearId
    ){
        SurveyRoundIdResponse response = surveyService.createRound(type, yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // PATCHMethod
}
