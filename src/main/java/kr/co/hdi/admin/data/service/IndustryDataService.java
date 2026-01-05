package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.request.IndustryDataRequest;
import kr.co.hdi.admin.data.dto.response.*;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.enums.IndustryImageType;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.data.repository.IndustryDataRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.YearRepository;
import kr.co.hdi.global.s3.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndustryDataService {

    private final ImageService imageService;

    private final YearRepository yearRepository;
    private final IndustryDataRepository industryDataRepository;

    /*
    산업 디자인 연도 목록 조회
     */
    public List<YearResponse> getIndustryDataYears() {

        List<Year> years = yearRepository.findAllByTypeAndDeletedAtIsNull(DomainType.INDUSTRY);

        return years.stream()
                .map(year -> {
                    LocalDateTime updatedAt = industryDataRepository
                            .findLastModifiedAtByYearId(year.getId())
                            .orElse(year.getUpdatedAt());
                    return YearResponse.from(year, updatedAt);
                })
                .toList();
    }

    /*
    산업 디자인 데이터셋 리스트 조회
     */
    public List<IndustryDataWithCategoryResponse> getIndustryDataList(Long yearId) {

        List<IndustryData> industryDatas = industryDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        return industryDatas.stream()
                .collect(Collectors.groupingBy(IndustryData::getIndustryDataCategory))
                .entrySet()
                .stream()
                .map(entry -> new IndustryDataWithCategoryResponse(
                        entry.getKey().name(),
                        entry.getValue().stream()
                                .map(i -> IndustryDataResponse.from(
                                        i,
                                        resolveIndustryImageUrl(i, IndustryImageType.DETAIL),
                                        resolveIndustryImageUrl(i, IndustryImageType.FRONT),
                                        resolveIndustryImageUrl(i, IndustryImageType.SIDE)
                                ))
                                .toList()
                ))
                .toList();
    }

    /*
    산업 디자인 데이터셋 조회
     */
    public IndustryDataResponse getIndustryData(Long dataId) {

        IndustryData industryData = industryDataRepository.findById(dataId)
                .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        return IndustryDataResponse.from(
                industryData,
                resolveIndustryImageUrl(industryData, IndustryImageType.DETAIL),
                resolveIndustryImageUrl(industryData, IndustryImageType.FRONT),
                resolveIndustryImageUrl(industryData, IndustryImageType.SIDE)
        );
    }

    private String resolveIndustryImageUrl(
            IndustryData data,
            IndustryImageType type
    ) {
        return switch (type) {
            case DETAIL -> data.getOriginalDetailImagePath() == null
                    ? null
                    : imageService.getImageUrl(data.getDetailImagePath());

            case FRONT -> data.getOriginalFrontImagePath() == null
                    ? null
                    : imageService.getImageUrl(data.getFrontImagePath());

            case SIDE -> data.getOriginalSideImagePath() == null
                    ? null
                    : imageService.getImageUrl(data.getSideImagePath());
        };
    }

    /*
    산업 전문가에게 매칭할 데이터셋 후보 조회
     */
    public List<IndustryDataIdsResponse> getIndustryDataIds(Long yearId) {

        return industryDataRepository.findIdByYearId(yearId);
    }

    /*
    산업 디자인 데이터셋 생성
     */
    @Transactional
    public IndustryImageUploadUrlResponse createIndustryData(Long yearId, IndustryDataRequest requst) {
        Year year = yearRepository.findByIdAndDeletedAtIsNull(yearId)
                .orElseThrow(() -> new DataException(DataErrorCode.YEAR_NOT_FOUND));

        IndustryData industryData = IndustryData.create(year, requst);
        industryDataRepository.save(industryData);

        return new IndustryImageUploadUrlResponse(
                imageService.generateUploadPresignedUrl(industryData.getDetailImagePath()),
                imageService.generateUploadPresignedUrl(industryData.getFrontImagePath()),
                imageService.generateUploadPresignedUrl(industryData.getSideImagePath())
        );
    }

    /*
    산업 디자인 데이터셋 수정
     */
    @Transactional
    public IndustryImageUploadUrlResponse updateIndustryData(Long datasetId, IndustryDataRequest request, List<String> image) {

        IndustryData industryData = industryDataRepository.findByIdAndDeletedAtIsNull(datasetId)
                .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        industryData.updatePartial(request);
        for(String imageStatus : image) {
            String key = industryData.deleteImage(imageStatus);
            if (key != null) {
                imageService.deleteImage(key);
            }
        }
        industryDataRepository.save(industryData);

        return new IndustryImageUploadUrlResponse(
                imageService.generateUploadPresignedUrl(industryData.getDetailImagePath()),
                imageService.generateUploadPresignedUrl(industryData.getFrontImagePath()),
                imageService.generateUploadPresignedUrl(industryData.getSideImagePath())
        );
    }

    /*
    산업 디자인 데이터셋 삭제
     */
    @Transactional
    public void deleteIndustryData(List<Long> ids) {

        List<IndustryData> industryDatas = industryDataRepository.findByIdInAndDeletedAtIsNull(ids);
        industryDatas.forEach(IndustryData::delete);
    }

    /*
    산업 디자인 데이터셋 복제
     */
    @Transactional
    public void duplicateIndustryData(List<Long> ids) {

        List<IndustryData> industryDatas = industryDataRepository.findByIdInAndDeletedAtIsNull(ids);
        List<IndustryData> duplicated = industryDatas.stream()
                .map(IndustryData::duplicate)
                .toList();
        industryDataRepository.saveAll(duplicated);
    }

    /*
    산업 디자인 데이터셋 엑셀 다운로드
     */
    public byte[] exportIndustryData(Long yearId) {
        List<IndustryData> rows = industryDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("industry_datasets");

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "ID", "Product Name", "Company Name", "Model Name",
                    "Price", "Material", "Size", "Weight",
                    "Reference URL", "Registered At",
                    "Product Path", "Product Type Name"
            };

            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < headers.length; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(headers[c]);
                cell.setCellStyle(headerStyle);
            }

            int r = 1;
            for (IndustryData i : rows) {
                Row row = sheet.createRow(r++);

                int c = 0;
                row.createCell(c++).setCellValue(nvl(i.getOriginalId()));
                row.createCell(c++).setCellValue(nvl(i.getProductName()));
                row.createCell(c++).setCellValue(nvl(i.getCompanyName()));
                row.createCell(c++).setCellValue(nvl(i.getModelName()));
                row.createCell(c++).setCellValue(nvl(i.getPrice()));
                row.createCell(c++).setCellValue(nvl(i.getMaterial()));
                row.createCell(c++).setCellValue(nvl(i.getSize()));
                row.createCell(c++).setCellValue(nvl(i.getWeight()));
                row.createCell(c++).setCellValue(nvl(i.getReferenceUrl()));
                row.createCell(c++).setCellValue(nvl(i.getRegisteredAt()));
                row.createCell(c++).setCellValue(nvl(i.getProductPath()));
                row.createCell(c++).setCellValue(nvl(i.getProductTypeName()));
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

    /*
    산업 디자인 데이터셋 검색
     */
    public List<IndustryDataResponse> searchIndustryData(String q, IndustryDataCategory category) {

        return industryDataRepository.search(q, category);
    }

    /*
    산업 디자인 데이터 이미지 다운로드
     */
    public void exportIndustryDataImages(List<Long> ids, OutputStream os) throws IOException {

        List<IndustryData> industryData = industryDataRepository.findByIdInAndDeletedAtIsNull(ids);

        Map<String, String> keyNameMap = new LinkedHashMap<>();

        for (IndustryData data : industryData) {

            putIfNotBlank(keyNameMap, data.getFrontImagePath(),  data.getOriginalFrontImagePath());
            putIfNotBlank(keyNameMap, data.getSideImagePath(),   data.getOriginalSideImagePath());
            putIfNotBlank(keyNameMap, data.getDetailImagePath(), data.getOriginalDetailImagePath());
        }

        imageService.downloadAsZip(keyNameMap, os);
    }

    private void putIfNotBlank(Map<String, String> map, String key, String originalName) {
        if (key == null || key.isBlank()) return;
        if (originalName == null || originalName.isBlank()) return;

        map.put(key, originalName);
    }
}
