package kr.co.hdi.admin.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.assignment.dto.request.AssignmentDataRequest;
import kr.co.hdi.admin.assignment.dto.response.AssessmentRoundResponse;
import kr.co.hdi.admin.assignment.dto.response.AssignmentResponse;
import kr.co.hdi.admin.assignment.service.AssignmentService;
import kr.co.hdi.admin.assignment.service.AssignmentServiceResolver;
import kr.co.hdi.admin.data.dto.request.DataIdsRequest;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/{type}/assignment")
@Tag(name = "데이터셋 할당", description = "데이터셋 할당 관리 API")
public class AssignmentController {

    private final AssignmentServiceResolver resolver;

    @GetMapping("/years")
    @Operation(summary = "매칭 연도 목록 조회")
    public ResponseEntity<List<YearResponse>> getAssignmentYearList(@PathVariable DomainType type) {

        AssignmentService service = resolver.resolve(type);
        List<YearResponse> responses = service.getAssignmentYearList();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/years/{yearId}")
    @Operation(summary = "해당 연도의 매칭 차수 목록 조회")
    public ResponseEntity<List<AssessmentRoundResponse>> getAssessmentRoundList(
            @PathVariable DomainType type,
            @PathVariable Long yearId) {

        AssignmentService service = resolver.resolve(type);
        List<AssessmentRoundResponse> responses = service.getAssessmentRoundList(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/assessment/{assessmentRoundId}")
    @Operation(summary = "해당 차수의 데이터셋 매칭 전체 조회")
    public ResponseEntity<List<AssignmentResponse>> getDatasetAssignment(
            @PathVariable DomainType type,
            @PathVariable Long assessmentRoundId) {

        AssignmentService service = resolver.resolve(type);
        List<AssignmentResponse> responses = service.getDatasetAssignment(assessmentRoundId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/assessment/{assessmentRoundId}/members/{memberId}")
    @Operation(summary = "데이터셋 매칭 전문가별 조회")
    public ResponseEntity<AssignmentResponse> getDatasetAssignmentByMember(
            @PathVariable DomainType type,
            @PathVariable Long assessmentRoundId,
            @PathVariable Long memberId
    ) {

        AssignmentService service = resolver.resolve(type);
        AssignmentResponse responses = service.getDatasetAssignmentByUser(assessmentRoundId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping("/assessment/{assessmentRoundId}/members/{memberId}")
    @Operation(summary = "데이터셋 매칭 수정")
    public ResponseEntity<Void> updateDatasetAssignment(
            @PathVariable DomainType type,
            @PathVariable Long assessmentRoundId,
            @PathVariable Long memberId,
            @RequestBody DataIdsRequest request
            ) {

        AssignmentService service = resolver.resolve(type);
        service.updateDatasetAssignment(assessmentRoundId, memberId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assessment/{assessmentRoundId}")
    @Operation(summary = "전문가와 데이터셋 매칭 등록")
    public ResponseEntity<Void> createDatasetAssignment(
            @PathVariable DomainType type,
            @PathVariable Long assessmentRoundId,
            @RequestBody AssignmentDataRequest request) {

        AssignmentService service = resolver.resolve(type);
        service.createDatasetAssignment(assessmentRoundId, request);
        return ResponseEntity.ok().build();
    }

}
