package kr.co.hdi.admin.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.data.dto.request.DataIdsRequest;
import kr.co.hdi.admin.data.dto.request.VisualDataRequest;
import kr.co.hdi.admin.data.dto.response.VisualDataIdsResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataWithCategoryResponse;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.admin.data.service.VisualDataService;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/visual/data")
@Tag(name = "시각디자인 데이터 ", description = "시각 디자인 데이터 관리 API")
public class VisualDataController {

    private final VisualDataService visualDataService;

    @GetMapping("/years")
    @Operation(summary = "시각 디자인 연도 목록 조회")
    public ResponseEntity<List<YearResponse>> getVisualDataYears() {

        List<YearResponse> response = visualDataService.getVisualDataYears();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/years/{yearId}/datasets")
    @Operation(summary = "시각 디자인 데이터셋 리스트 조회")
    public ResponseEntity<List<VisualDataWithCategoryResponse>> getVisualDataList(@PathVariable Long yearId) {

        List<VisualDataWithCategoryResponse> response = visualDataService.getVisualDataList(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/datasets/{datasetId}")
    @Operation(summary = "시각 디자인 데이터셋 조회")
    public ResponseEntity<VisualDataResponse> getVisualData(@PathVariable Long datasetId) {

        VisualDataResponse response = visualDataService.getVisualData(datasetId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/years/{yearId}/datasets/id")
    @Operation(summary = "시각 전문가에게 매칭할 데이터셋 후보 조회")
    public ResponseEntity<List<VisualDataIdsResponse>> getVisualDataIds(@PathVariable Long yearId) {

        List<VisualDataIdsResponse> response = visualDataService.getVisualDataIds(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/datasets/duplicate")
    @Operation(summary = "시각 디자인 데이터셋 복제")
    public ResponseEntity<Void> duplicateVisualData(@RequestBody DataIdsRequest request) {

        visualDataService.duplicateVisualData(request.ids());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/years/{yearId}/datasets")
    @Operation(summary = "시각 디자인 데이터셋 생성")
    public ResponseEntity<Void> createVisualData(
            @PathVariable Long yearId,
            @RequestBody VisualDataRequest request) {

        visualDataService.createVisualData(yearId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/datasets/{datasetId}")
    @Operation(summary = "시각 디자인 데이터셋 수정")
    public ResponseEntity<Void> updateVisualData(
            @PathVariable Long datasetId,
            @RequestBody VisualDataRequest request) {

        visualDataService.updateVisualData(datasetId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/datasets")
    @Operation(summary = "시각 디자인 데이터셋 삭제")
    public ResponseEntity<Void> deleteVisualData(@RequestBody DataIdsRequest request) {

        visualDataService.deleteVisualData(request.ids());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/datasets/search")
    @Operation(summary = "시각 디자인 데이터셋 검색")
    public ResponseEntity<List<VisualDataResponse>> searchVisualData(
            @RequestParam String q, @RequestParam VisualDataCategory category) {

        List<VisualDataResponse> response = visualDataService.searchVisualData(q, category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/years/{yearId}/datasets/export")
    @Operation(summary = "시각 디자인 데이터셋 액셀 다운로드")
    public ResponseEntity<Resource> exportVisualData(
            @PathVariable("yearId") Long yearId) {

        byte[] bytes = visualDataService.exportVisualData(yearId);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "visual_data.xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentLength(bytes.length)
                .body(resource);
    }
}
