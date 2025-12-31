package kr.co.hdi.admin.data.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.hdi.admin.data.dto.request.DataIdsRequest;
import kr.co.hdi.admin.data.dto.request.IndustryDataRequest;
import kr.co.hdi.admin.data.dto.response.*;
import kr.co.hdi.admin.data.service.IndustryDataService;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/industry/data")
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
    public ResponseEntity<List<IndustryDataWithCategoryResponse>> getIndustryDataList(
            @PathVariable("yearId") Long yearId) {

        List<IndustryDataWithCategoryResponse> response = industryDataService.getIndustryDataList(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/datasets/{datasetId}")
    @Operation(summary = "산업 디자인 데이터셋 조회")
    public ResponseEntity<IndustryDataResponse> getIndustryData(
            @PathVariable("datasetId") Long datasetId) {

        IndustryDataResponse response = industryDataService.getIndustryData(datasetId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/years/{yearId}/datasets/id")
    @Operation(summary = "산업 전문가에게 매칭할 데이터셋 후보 조회")
    public ResponseEntity<List<IndustryDataIdsResponse>> getIndustryDataIds(
            @PathVariable("yearId") Long yearId) {

        List<IndustryDataIdsResponse> response = industryDataService.getIndustryDataIds(yearId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/years/{yearId}/datasets/export")
    @Operation(summary = "산업 디자인 데이터셋 액셀 다운로드")
    public ResponseEntity<Resource>  exportIndustryData(
            @PathVariable("yearId") Long yearId) throws IOException {

        byte[] bytes = industryDataService.exportIndustryData(yearId);
        ByteArrayResource resource = new ByteArrayResource(bytes);
        String filename = "industry_data.xlsx";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                .contentLength(bytes.length)
                .body(resource);
    }

    @GetMapping("/datasets/search")
    @Operation(summary = "산업 디자인 데이터셋 검색")
    public ResponseEntity<List<IndustryDataResponse>> searchIndustryData(
            @RequestParam String q, @RequestParam IndustryDataCategory category) {

        List<IndustryDataResponse> response = industryDataService.searchIndustryData(q, category);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /*
    POST method
    */
    @PostMapping("/years/{yearId}/datasets")
    @Operation(summary = "산업 디자인 데이터셋 생성")
    public ResponseEntity<IndustryImageUploadUrlResponse> createIndustryData(
            @PathVariable Long yearId,
            @RequestBody IndustryDataRequest request) {

        IndustryImageUploadUrlResponse response = industryDataService.createIndustryData(yearId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/datasets/duplicate")
    @Operation(summary = "산업 디자인 데이터셋 복제")
    public ResponseEntity<Void> duplicateIndustryData(
            @RequestBody DataIdsRequest request) {

        industryDataService.duplicateIndustryData(request.ids());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/datasets/image/export")
    @Operation(summary = "산업 디자인 이미지 데이터 zip 다운로드")
    public void exportIndustryDataImages(
            @RequestBody DataIdsRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=industry_images.zip");

        industryDataService.exportIndustryDataImages(request.ids(), response.getOutputStream());

        response.flushBuffer();
    }

    /*
    PATCH method
    */
    @PatchMapping("/datasets/{datasetId}")
    @Operation(summary = "산업 디자인 데이터셋 수정")
    public ResponseEntity<IndustryImageUploadUrlResponse> updateIndustryData(
            @PathVariable("datasetId") Long datasetId,
            @RequestBody IndustryDataRequest request,
            @RequestParam(defaultValue = "") List<String> image) {

        IndustryImageUploadUrlResponse response = industryDataService.updateIndustryData(datasetId, request, image);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /*
    DELETE method
    */
    @DeleteMapping("/datasets")
    @Operation(summary = "산업 디자인 데이터셋 삭제")
    public ResponseEntity<Void> deleteIndustryData(
            @RequestBody DataIdsRequest request) {

        industryDataService.deleteIndustryData(request.ids());
        return ResponseEntity.ok().build();
    }


}
