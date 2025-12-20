package kr.co.hdi.admin.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.assignment.dto.request.AssignmentDataRequest;
import kr.co.hdi.admin.assignment.dto.response.AssessmentRoundResponse;
import kr.co.hdi.admin.assignment.dto.response.AssignmentResponse;
import kr.co.hdi.admin.assignment.service.VisualAssignmentService;
import kr.co.hdi.admin.data.dto.request.VisualDataIdsRequest;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.domain.user.entity.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{type}/assignment")
@Tag(name = "데이터셋 할당", description = "데이터셋 할당 관리 API")
public class AssignmentController {

    private final VisualAssignmentService visualAssignmentService;

    @GetMapping("/years")
    @Operation(summary = "매칭 연도 목록 조회")
    public ResponseEntity<List<YearResponse>> getAssignmentYearList(@PathVariable UserType type) {

        List<YearResponse> responses = visualAssignmentService.getAssignmentYearList();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/years/{yearId}")
    @Operation(summary = "해당 연도의 매칭 차수 목록 조회")
    public ResponseEntity<List<AssessmentRoundResponse>> getAssessmentRoundList(
            @PathVariable UserType type,
            @PathVariable Long yearId) {

        List<AssessmentRoundResponse> responses = visualAssignmentService.getAssessmentRoundList(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/assessment/{assessmentRoundId}")
    @Operation(summary = "해당 차수의 데이터셋 매칭 전체 조회")
    public ResponseEntity<List<AssignmentResponse>> getDatasetAssignment(
            @PathVariable UserType type,
            @PathVariable Long assessmentRoundId) {

        List<AssignmentResponse> responses = visualAssignmentService.getDatasetAssignment(assessmentRoundId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/assessment/{assessmentRoundId}/members/{memberId}")
    @Operation(summary = "데이터셋 매칭 전문가별 조회")
    public ResponseEntity<AssignmentResponse> getDatasetAssignmentByMember(
            @PathVariable UserType type,
            @PathVariable Long assessmentRoundId,
            @PathVariable Long memberId
    ) {

        AssignmentResponse responses = visualAssignmentService.getDatasetAssignmentByUser(assessmentRoundId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping("/assessment/{assessmentRoundId}/members/{memberId}")
    @Operation(summary = "데이터셋 매칭 수정")
    public ResponseEntity<Void> updateDatasetAssignment(
            @PathVariable UserType type,
            @PathVariable Long assessmentRoundId,
            @PathVariable Long memberId,
            @RequestBody VisualDataIdsRequest request
            ) {

        visualAssignmentService.updateDatasetAssignment(assessmentRoundId, memberId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assessment/{assessmentRoundId}")
    @Operation(summary = "전문가와 데이터셋 매칭 등록")
    public ResponseEntity<Void> createDatasetAssignment(
            @PathVariable UserType type,
            @PathVariable Long assessmentRoundId,
            @RequestBody AssignmentDataRequest request) {

        visualAssignmentService.createDatasetAssignment(assessmentRoundId, request);
        return ResponseEntity.ok().build();
    }

}
