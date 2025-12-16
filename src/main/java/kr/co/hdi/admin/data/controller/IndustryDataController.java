package kr.co.hdi.admin.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.hdi.admin.data.dto.response.*;
import kr.co.hdi.admin.data.service.IndustryDataService;
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
@RequestMapping("/api/v1/industry/data")
@Tag(name = "산업디자인 데이터 ", description = "산업 디자인 데이터 관리 API")
public class IndustryDataController {

    private final IndustryDataService industryDataService;

    /*
    GET method
    */

    @GetMapping("/years")
    @Operation(summary = "산업 디자인 연도 목록 조회")
    public ResponseEntity<List<YearResponse>> getIndustryDataYears() {

        List<YearResponse> response = industryDataService.getIndustryDataYears();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/years/{yearId}/datasets")
    @Operation(summary = "산업 디자인 데이터셋 리스트 조회")
    public ResponseEntity<List<IndustryDataWithCategoryResponse>> getIndustryDataList(@PathVariable("yearId") Long yearId) {

        List<IndustryDataWithCategoryResponse> response = industryDataService.getIndustryDataList(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/datasets/{datasetId}")
    @Operation(summary = "산업 디자인 데이터셋 조회")
    public ResponseEntity<IndustryDataResponse> getIndustryData(@PathVariable("datasetId") Long datasetId) {

        IndustryDataResponse response = industryDataService.getIndustryData(datasetId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/years/{yearId}/datasets/id")
    @Operation(summary = "산업 전문가에게 매칭할 데이터셋 후보 조회")
    public ResponseEntity<List<IndustryDataIdsResponse>> getIndustryDataIds(@PathVariable("yearId") Long yearId) {

        List<IndustryDataIdsResponse> response = industryDataService.getIndustryDataIds(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
