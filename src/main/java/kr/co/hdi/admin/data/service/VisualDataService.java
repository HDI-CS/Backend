package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.request.DataIdsRequest;
import kr.co.hdi.admin.data.dto.request.VisualDataRequest;
import kr.co.hdi.admin.data.dto.response.*;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.YearRepository;
import kr.co.hdi.global.s3.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisualDataService {

    private final ImageService imageService;

    private final YearRepository yearRepository;
    private final VisualDataRepository visualDataRepository;

    /*
    시각 디자인 연도 목록 조회
     */
    public List<YearResponse> getVisualDataYears() {

        List<Year> years = yearRepository.findAllByTypeAndDeletedAtIsNullOrderByCreatedAtAsc(DomainType.VISUAL);

        return years.stream()
                .map(year -> {
                    LocalDateTime updatedAt = visualDataRepository
                            .findLastModifiedAtByYearId(year.getId())
                            .orElse(year.getUpdatedAt());
                    return YearResponse.from(year, updatedAt);
                })
                .toList();
    }

    /*
    시각 디자인 데이터셋 리스트 조회
     */
    public List<VisualDataWithCategoryResponse> getVisualDataList(@PathVariable Long yearId) {

        List<VisualData> visualDatas = visualDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        return visualDatas.stream()
                .collect(Collectors.groupingBy(VisualData::getVisualDataCategory))
                .entrySet()
                .stream()
                .map(entry -> new VisualDataWithCategoryResponse(
                        entry.getKey().name(),
                        entry.getValue().stream()
                                .map(v -> VisualDataResponse.from(
                                    v,
                                    resolveImageUrl(v)
                                ))
                                .toList()
                ))
                .toList();
    }

    /*
    시각 디자인 데이터셋 조회
     */
    public VisualDataResponse getVisualData(@PathVariable Long datasetId) {

        VisualData visualData = visualDataRepository.findById(datasetId)
                .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        String imageUrl = resolveImageUrl(visualData);
        return VisualDataResponse.from(visualData, imageUrl);
    }

    private String resolveImageUrl(VisualData visualData) {
        if (visualData.getOriginalLogoImage() == null) {
            return null;
        }
        return imageService.getImageUrl(visualData.getLogoImage());
    }

    /*
    시각 전문가에게 매칭할 데이터셋 후보 조회
     */
    public List<VisualDataIdsResponse> getVisualDataIds(@PathVariable Long yearId) {

        return visualDataRepository.findIdByYearId(yearId);
    }

    /*
    시각 디자인 데이터셋 복제
     */
    @Transactional
    public void duplicateVisualData(List<Long> ids) {

        List<VisualData> visualDatas = visualDataRepository.findByIdInAndDeletedAtIsNull(ids);
        List<VisualData> duplicated = visualDatas.stream()
                .map(VisualData::duplicate)
                .toList();
        visualDataRepository.saveAll(duplicated);
    }

    /*
    시각 디자인 데이터셋 생성
     */
    @Transactional
    public VisualImageUploadUrlResponse createVisualData(Long yearId, VisualDataRequest request) {

        Year year = yearRepository.findByIdAndDeletedAtIsNull(yearId)
                .orElseThrow(() -> new DataException(DataErrorCode.YEAR_NOT_FOUND));

        VisualData visualData = VisualData.create(year, request);
        visualDataRepository.save(visualData);

        String imageUploadUrl = imageService.generateUploadPresignedUrl(visualData.getLogoImage());
        return new VisualImageUploadUrlResponse(imageUploadUrl);
    }

    /*
    시각 디자인 데이터셋 수정
     */
    @Transactional
    public VisualImageUploadUrlResponse updateVisualData(Long datasetId, VisualDataRequest request, String image) {

        VisualData visualData = visualDataRepository.findByIdAndDeletedAtIsNull(datasetId)
                        .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        visualData.updatePartial(request);
        if (image.equals("DELETE")) {
            visualData.deleteImage();
            imageService.deleteImage(visualData.getLogoImage());
        }
        visualDataRepository.save(visualData);

        String imageUploadUrl = imageService.generateUploadPresignedUrl(visualData.getLogoImage());
        return new VisualImageUploadUrlResponse(imageUploadUrl);
    }

    /*
    시각 디자인 데이터셋 삭제
     */
    @Transactional
    public void deleteVisualData(List<Long> ids) {

        List<VisualData> visualDatas = visualDataRepository.findByIdInAndDeletedAtIsNull(ids);
        visualDatas.forEach(VisualData::delete);
    }

    /*
    시각 디자인 데이터셋 검색
     */
    public List<VisualDataResponse> searchVisualData(String q, VisualDataCategory category) {

        return visualDataRepository.search(q, category);
    }

    /*
    시각 디자인 데이터 이미지 다운로드
     */
    public void exportVisualDataImages(List<Long> ids, OutputStream os) throws IOException {

        List<VisualData> visualDatas = visualDataRepository.findByIdInAndDeletedAtIsNull(ids);

        Map<String, String> keyNameMap = visualDatas.stream()
                .collect(Collectors.toMap(
                        VisualData::getLogoImage,
                        VisualData::getOriginalLogoImage
                ));

        imageService.downloadAsZip(keyNameMap, os);
    }

    /*
    시각 디자인 데이터셋 액셀 다운로드
     */
    public byte[] exportVisualData(Long yearId) {
        List<VisualData> rows = visualDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("visual_datasets");

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "ID", "Brand Name", "Sector Category", "Main Product Category",
                    "Main Product", "Target", "Reference URL", "Category"
            };

            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < headers.length; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(headers[c]);
                cell.setCellStyle(headerStyle);
            }

            int r = 1;
            for (VisualData i : rows) {
                Row row = sheet.createRow(r++);

                int c = 0;
                row.createCell(c++).setCellValue(nvl(i.getBrandCode()));
                row.createCell(c++).setCellValue(nvl(i.getBrandName()));
                row.createCell(c++).setCellValue(nvl(i.getSectorCategory()));
                row.createCell(c++).setCellValue(nvl(i.getMainProductCategory()));
                row.createCell(c++).setCellValue(nvl(i.getMainProduct()));
                row.createCell(c++).setCellValue(nvl(i.getTarget()));
                row.createCell(c++).setCellValue(nvl(i.getReferenceUrl()));
                row.createCell(c++).setCellValue(nvl(i.getVisualDataCategory()));
            }

            for (int c = 0; c < headers.length; c++) {
                sheet.autoSizeColumn(c);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to export excel", e);
        }
    }

    private String nvl(Object v) {
        return v == null ? "" : String.valueOf(v);
    }
}
