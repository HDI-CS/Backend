package kr.co.hdi.admin.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.hdi.admin.data.dto.request.VisualCategoryFieldSpecRequest;
import kr.co.hdi.admin.data.dto.response.VisualCategoryFieldSpecResponse;
import kr.co.hdi.admin.data.service.VisualCategoryFieldSpecService;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/visual/category-field-specs")
@Tag(name = "시각디자인 카테고리 필드 정의", description = "카테고리별 가변 스펙(attributes) 필드 정의 관리 API")
public class VisualCategoryFieldSpecController {

    private final VisualCategoryFieldSpecService service;

    @GetMapping
    @Operation(summary = "카테고리의 스펙 필드 목록 조회 (연도 지정 시 해당 연도 override 우선, 없으면 공통)")
    public ResponseEntity<List<VisualCategoryFieldSpecResponse>> getSpecs(
            @RequestParam VisualDataCategory category,
            @RequestParam(required = false) Long yearId
    ) {
        List<VisualCategoryFieldSpecResponse> response = service.getSpecs(category, yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    @Operation(summary = "카테고리에 스펙 필드 정의 추가 (yearId 생략 시 해당 카테고리 전체 연도 공통)")
    public ResponseEntity<VisualCategoryFieldSpecResponse> createSpec(
            @RequestParam VisualDataCategory category,
            @RequestParam(required = false) Long yearId,
            @Valid @RequestBody VisualCategoryFieldSpecRequest request
    ) {
        VisualCategoryFieldSpecResponse response = service.createSpec(category, yearId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
