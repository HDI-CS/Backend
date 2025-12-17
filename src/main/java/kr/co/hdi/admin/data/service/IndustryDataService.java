package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.request.IndustryDataRequest;
import kr.co.hdi.admin.data.dto.response.*;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.data.repository.IndustryDataRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndustryDataService {

    private final YearRepository yearRepository;
    private final IndustryDataRepository industryDataRepository;

    /*
    산업 디자인 연도 목록 조회
     */
    public List<YearResponse> getIndustryDataYears() {

        List<Year> years = yearRepository.findAll();
        return years.stream()
                .map(YearResponse::from)
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
                                .map(IndustryDataResponse::from)
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

        return IndustryDataResponse.from(industryData);
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
    public void createIndustryData(Long yearId, IndustryDataRequest requst) {
        Year year = yearRepository.findByIdAndDeletedAtIsNull(yearId)
                .orElseThrow(() -> new DataException(DataErrorCode.YEAR_NOT_FOUND));

        IndustryData industryData = IndustryData.create(year, requst);
        industryDataRepository.save(industryData);
    }

    /*
    산업 디자인 데이터셋 수정
     */
    @Transactional
    public void updateIndustryData(Long datasetId, IndustryDataRequest request) {

        IndustryData industryData = industryDataRepository.findByIdAndDeletedAtIsNull(datasetId)
                .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        industryData.updatePartial(request);
        industryDataRepository.save(industryData);
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
                row.createCell(c++).setCellValue(nvl(i.getId()));
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
}
