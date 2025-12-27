package kr.co.hdi.admin.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.assignment.service.AssignmentService;
import kr.co.hdi.admin.survey.dto.request.SurveyContentResquest;
import kr.co.hdi.admin.survey.dto.request.SurveyDateRequest;
import kr.co.hdi.admin.survey.dto.request.SurveyFolderNameRequest;
import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
import kr.co.hdi.admin.survey.dto.response.SurveyQuestionsByYearResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyRoundIdResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyYearIdResponse;
import kr.co.hdi.admin.survey.service.SurveyService;
import kr.co.hdi.admin.survey.service.SurveyServiceResolver;
import kr.co.hdi.domain.year.entity.AssessmentRound;
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

    private final SurveyServiceResolver resolver;

    // GET Method
    @GetMapping("/all")
    @Operation(summary = "전체 평가 조회")
    public ResponseEntity<List<SurveyResponse>> getSurveys(
            @PathVariable DomainType type){
        SurveyService surveyService = resolver.resolve(type);
        List<SurveyResponse> responses = surveyService.getSurveys(type);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/years/{yearId}/questions")
    @Operation(summary = "년도 평가 설문 문항 조회")
    public ResponseEntity<SurveyQuestionsByYearResponse> getSurveyQuestions(
            @PathVariable DomainType type,
            @PathVariable Long yearId){
        SurveyService surveyService = resolver.resolve(type);
        SurveyQuestionsByYearResponse response = surveyService.getSurveyQuestions(type, yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // POST Method
    @PostMapping
    @Operation(summary = "년도 평가 생성")
    public ResponseEntity<SurveyYearIdResponse> createSurvey(
            @PathVariable DomainType type
    ){
        SurveyService surveyService = resolver.resolve(type);
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
        SurveyService surveyService = resolver.resolve(type);
        surveyService.createSurveyQuestion(type, yearId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/years/{yearId}/assessment")
    @Operation(summary = "차수 평가 생성")
    public ResponseEntity<SurveyRoundIdResponse> createRound(
            @PathVariable DomainType type,
            @PathVariable Long yearId
    ){
        SurveyService surveyService = resolver.resolve(type);
        SurveyRoundIdResponse response = surveyService.createRound(type, yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // PUTMethod
    @PutMapping("/years/{yearId}")
    @Operation(summary = "년도 평가 이름 수정")
    public ResponseEntity<?> updateYearFolderName(
            @PathVariable DomainType type,
            @PathVariable Long yearId,
            @RequestBody SurveyFolderNameRequest request
    ){
        SurveyService surveyService = resolver.resolve(type);
        surveyService.updateYearFolderName(type, yearId, request.folderName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/question/{questionId}")
    @Operation(summary = "년도 평가 단일 설문 문항 수정")
    public ResponseEntity<?> updateSurveyContent(
            @PathVariable DomainType type,
            @PathVariable Long questionId,
            @RequestBody SurveyContentResquest request
    ){
        SurveyService surveyService = resolver.resolve(type);
        surveyService.updateSurveyContent(type,questionId, request.surveyContent());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/assessment/{assessmentRoundId}")
    @Operation(summary = "차수 평가 이름 수정")
    public ResponseEntity<?> updateRoundFolderName(
            @PathVariable DomainType type,
            @PathVariable Long assessmentRoundId,
            @RequestBody SurveyFolderNameRequest request
    ){
        SurveyService surveyService = resolver.resolve(type);
        surveyService.updateRoundFolderName(type, assessmentRoundId, request.folderName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/assessment/{assessmentRoundId}/duration")
    @Operation(summary = "차수 평가 기간 생성 및 수정")
    public ResponseEntity<?> upsertSurveyDate(
            @PathVariable DomainType type,
            @PathVariable Long assessmentRoundId,
            @RequestBody SurveyDateRequest request
    ){
        SurveyService surveyService = resolver.resolve(type);
        surveyService.upsertSurveyDate(type, assessmentRoundId, request);
        return ResponseEntity.ok().build();
    }

}
