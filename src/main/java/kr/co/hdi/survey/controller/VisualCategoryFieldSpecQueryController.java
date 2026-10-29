package kr.co.hdi.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.data.dto.response.VisualCategoryFieldSpecResponse;
import kr.co.hdi.admin.data.service.VisualCategoryFieldSpecService;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
평가자 화면에서 attributes(추가 스펙)를 "키 그대로"가 아니라 라벨로 보여주기 위한
읽기 전용 엔드포인트. IndustryCategoryFieldSpecQueryController와 동일한 패턴.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/visual/category-field-specs")
@Tag(name = "시각디자인 카테고리 필드 정의 (평가자 조회용)", description = "attributes 라벨 조회 API")
public class VisualCategoryFieldSpecQueryController {

    private final VisualCategoryFieldSpecService service;

    @GetMapping
    @Operation(summary = "카테고리의 스펙 필드 목록 조회 (평가자 화면 라벨 표시용, 연도 무관)")
    public ResponseEntity<List<VisualCategoryFieldSpecResponse>> getSpecs(
            @RequestParam VisualDataCategory category
    ) {
        List<VisualCategoryFieldSpecResponse> response = service.getSpecsForDisplay(category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
