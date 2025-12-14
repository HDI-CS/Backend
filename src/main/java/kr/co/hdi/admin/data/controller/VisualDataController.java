package kr.co.hdi.admin.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.data.dto.response.VisualDataIdsResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataWithCategoryResponse;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.admin.data.service.VisualDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/data")
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
}
