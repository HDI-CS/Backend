package kr.co.hdi.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.co.hdi.survey.dto.request.WeightedScoreRequest;
import kr.co.hdi.survey.dto.response.WeightedScoreResponse;
import kr.co.hdi.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/survey")
public class CommonSurveyController {

    private final SurveyService surveyService;

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
