package kr.co.hdi.admin.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.hdi.admin.user.dto.request.ExpertInfoRequest;
import kr.co.hdi.admin.user.dto.request.ExpertInfoUpdateRequest;
import kr.co.hdi.admin.user.dto.response.ExpertInfoResponse;
import kr.co.hdi.admin.user.dto.response.ExpertNameResponse;
import kr.co.hdi.admin.user.service.ExpertUserService;
import kr.co.hdi.domain.user.entity.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/{type}/members")
public class ExpertUserController {

    private final ExpertUserService expertUserService;

    @GetMapping
    @Operation(summary = "전문가 리스트 조회")
    public ResponseEntity<List<ExpertInfoResponse>> getExpertInfo(@PathVariable UserType type) {

        List<ExpertInfoResponse> responses = expertUserService.getExpertInfo(type);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PostMapping
    @Operation(summary = "전문가 인적사항 등록")
    public ResponseEntity<Void> registerExpert(
            @PathVariable UserType type,
            @RequestBody ExpertInfoRequest request) {

        expertUserService.registerExpert(type, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "전문가 인적사항 수정")
    public ResponseEntity<Void> updateExpertInfo(
            @PathVariable UserType type,
            @PathVariable Long memberId,
            @RequestBody ExpertInfoUpdateRequest request) {

        expertUserService.updateExpertInfo(request, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "평가에 참여할 전문가 후보 검색")
    public ResponseEntity<List<ExpertNameResponse>> searchExpertByName(
            @PathVariable UserType type,
            @RequestParam String q) {

        List<ExpertNameResponse> responses = expertUserService.searchExpertByName(type, q);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
