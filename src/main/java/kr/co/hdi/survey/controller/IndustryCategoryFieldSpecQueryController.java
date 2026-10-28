package kr.co.hdi.survey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.data.dto.response.IndustryCategoryFieldSpecResponse;
import kr.co.hdi.admin.data.service.IndustryCategoryFieldSpecService;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
평가자 화면에서 제품의 attributes(추가 스펙)를 "키 그대로"가 아니라 라벨로 보여주기 위해
읽기 전용으로 필요한 엔드포인트. 스펙 정의 자체(생성)는 관리자만 할 수 있어서
/api/v1/admin/industry/category-field-specs에만 POST가 있고, 여긴 GET만 있다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/industry/category-field-specs")
@Tag(name = "산업디자인 카테고리 필드 정의 (평가자 조회용)", description = "attributes 라벨 조회 API")
public class IndustryCategoryFieldSpecQueryController {

    private final IndustryCategoryFieldSpecService service;

    @GetMapping
    @Operation(summary = "카테고리의 스펙 필드 목록 조회 (평가자 화면 라벨 표시용, 연도 무관)")
    public ResponseEntity<List<IndustryCategoryFieldSpecResponse>> getSpecs(
            @RequestParam IndustryDataCategory category
    ) {
        List<IndustryCategoryFieldSpecResponse> response = service.getSpecsForDisplay(category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
