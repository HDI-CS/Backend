package kr.co.hdi.crawl.excel;

import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.repository.IndustryDataRepository;
import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.response.entity.IndustryWeightedScore;
import kr.co.hdi.domain.response.repository.IndustryResponseRepository;
import kr.co.hdi.domain.response.repository.IndustryWeightedScoreRepository;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.repository.IndustrySurveyRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndustryResultExcelService {

    private final IndustryDataRepository industryDataRepository;
    private final IndustrySurveyRepository industrySurveyRepository;
    private final IndustryResponseRepository industryResponseRepository;
    private final IndustryWeightedScoreRepository industryWeightedScoreRepository;

    public byte[] exportIndustryResultByTeam(Long yearId) {

        List<IndustryData> dataList =
                industryDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        List<IndustrySurvey> surveys =
                industrySurveyRepository.findAllByYear(yearId);

        List<IndustryResponse> responses =
                industryResponseRepository.findAllEntitiesByAssessmentRoundId(8L);

        List<IndustryWeightedScore> weights =
                industryWeightedScoreRepository.findAllByYearId(yearId);

        Map<String, IndustryWeightedScore> weightMapByUserYearRoundIdAndCategory =
                weights.stream()
                        .filter(w -> w.getUserYearRound() != null)
                        .filter(w -> w.getIndustryDataCategory() != null)
                        .collect(Collectors.toMap(
                                w -> weightKey(
                                        w.getUserYearRound().getId(),
                                        w.getIndustryDataCategory()
                                ),
                                w -> w,
                                (w1, w2) -> w1
                        ));

        System.out.println("산업 가중치 개수 = " + weights.size());
        System.out.println("산업 가중치 keys = " + weightMapByUserYearRoundIdAndCategory.keySet());

        // =====================
        // 설문 문항 정렬
        // - PR_TEXT는 점수 컬럼에서 제외
        // - surveyNumber 오름차순
        // - 헤더 값: "14. PR_USB_FL" 형태
        // =====================
        List<IndustrySurvey> sortedSurveys = surveys.stream()
                .filter(s -> s.getSurveyCode() != null && !s.getSurveyCode().isBlank())
                .filter(s -> !"PR_TEXT".equals(s.getSurveyCode()))
                .sorted(Comparator.comparing(
                        IndustrySurvey::getSurveyNumber,
                        Comparator.nullsLast(Integer::compareTo)
                ))
                .toList();

        Map<String, Map<Long, List<IndustryResponse>>> teamDataMap =
                responses.stream()
                        .filter(r -> {
                            String name = safeName(r.getUserYearRound().getUser().getName());
                            String team = getTeam(name);
                            return team != null && !team.isBlank();
                        })
                        .collect(Collectors.groupingBy(
                                r -> {
                                    String name = safeName(r.getUserYearRound().getUser().getName());
                                    String team = getTeam(name);
                                    return "Team " + team + "." + name;
                                },
                                Collectors.groupingBy(
                                        r -> r.getIndustryData().getId()
                                )
                        ));

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            short rowHeight = 400;

            // =====================
            // 위쪽 설문 헤더 스타일 - 파란색 배경
            // =====================
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // =====================
            // 일반 값 스타일
            // =====================
            CellStyle valueStyle = wb.createCellStyle();
            valueStyle.setAlignment(HorizontalAlignment.RIGHT);
            valueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            valueStyle.setBorderTop(BorderStyle.THIN);
            valueStyle.setBorderBottom(BorderStyle.THIN);
            valueStyle.setBorderLeft(BorderStyle.THIN);
            valueStyle.setBorderRight(BorderStyle.THIN);

            // =====================
            // ID 값 스타일
            // =====================
            CellStyle idValueStyle = wb.createCellStyle();
            idValueStyle.setAlignment(HorizontalAlignment.CENTER);
            idValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            idValueStyle.setBorderTop(BorderStyle.THIN);
            idValueStyle.setBorderBottom(BorderStyle.THIN);
            idValueStyle.setBorderLeft(BorderStyle.THIN);
            idValueStyle.setBorderRight(BorderStyle.THIN);

            // =====================
            // 텍스트 줄바꿈 스타일
            // =====================
            CellStyle wrapStyle = wb.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            wrapStyle.setBorderTop(BorderStyle.THIN);
            wrapStyle.setBorderBottom(BorderStyle.THIN);
            wrapStyle.setBorderLeft(BorderStyle.THIN);
            wrapStyle.setBorderRight(BorderStyle.THIN);

            // =====================
            // 가중치 헤더 스타일 - 파란색 배경
            // =====================
            CellStyle weightHeaderStyle = wb.createCellStyle();
            Font weightFont = wb.createFont();
            weightFont.setBold(true);
            weightFont.setColor(IndexedColors.WHITE.getIndex());
            weightHeaderStyle.setFont(weightFont);
            weightHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            weightHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            weightHeaderStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            weightHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            weightHeaderStyle.setBorderTop(BorderStyle.THIN);
            weightHeaderStyle.setBorderBottom(BorderStyle.THIN);
            weightHeaderStyle.setBorderLeft(BorderStyle.THIN);
            weightHeaderStyle.setBorderRight(BorderStyle.THIN);

            // =====================
            // 가중치 값 스타일
            // =====================
            CellStyle weightValueStyle = wb.createCellStyle();
            weightValueStyle.setAlignment(HorizontalAlignment.CENTER);
            weightValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            weightValueStyle.setBorderTop(BorderStyle.THIN);
            weightValueStyle.setBorderBottom(BorderStyle.THIN);
            weightValueStyle.setBorderLeft(BorderStyle.THIN);
            weightValueStyle.setBorderRight(BorderStyle.THIN);

            List<String> sortedSheetNames = teamDataMap.keySet().stream()
                    .sorted(
                            Comparator
                                    .comparingInt(this::extractTeamNumber)
                                    .thenComparing(this::extractUserNameFromSheetName)
                    )
                    .toList();

            for (String sheetName : sortedSheetNames) {

                Sheet sheet = wb.createSheet(safeSheetName(sheetName));
                int rIdx = 0;

                // =====================
                // 1. 설문 평가 헤더
                // =====================
                Row header = sheet.createRow(rIdx++);
                header.setHeight(rowHeight);

                int c = 0;

                Cell idHeaderCell = header.createCell(c++);
                idHeaderCell.setCellValue("ID");
                idHeaderCell.setCellStyle(headerStyle);

                for (IndustrySurvey survey : sortedSurveys) {
                    Cell cell = header.createCell(c++);
                    cell.setCellValue(survey.getSurveyNumber() + ". " + survey.getSurveyCode());
                    cell.setCellStyle(headerStyle);
                }

                Cell textHeaderCell = header.createCell(c++);
                textHeaderCell.setCellValue("정성평가");
                textHeaderCell.setCellStyle(headerStyle);

                // =====================
                // 2. 설문 평가 데이터
                // =====================
                Map<Long, List<IndustryResponse>> dataMap = teamDataMap.get(sheetName);

                List<Long> sortedDataIds = dataMap.keySet().stream()
                        .sorted(Comparator.comparing(dataId -> {
                            IndustryData data = findIndustryData(dataList, dataId);
                            return getCodeNumber(data);
                        }))
                        .toList();

                for (Long dataId : sortedDataIds) {

                    IndustryData data = findIndustryData(dataList, dataId);
                    if (data == null) continue;

                    Row row = sheet.createRow(rIdx++);
                    row.setHeight(rowHeight);

                    int col = 0;

                    Cell idCell = row.createCell(col++);
                    idCell.setCellValue(nvl(getIndustryCode(data)));
                    idCell.setCellStyle(idValueStyle);

                    List<IndustryResponse> responseList = dataMap.get(dataId);

                    for (IndustrySurvey survey : sortedSurveys) {

                        List<IndustryResponse> filtered = responseList.stream()
                                .filter(v -> v.getIndustrySurvey() != null)
                                .filter(v -> survey.getId().equals(v.getIndustrySurvey().getId()))
                                .toList();

                        Cell valueCell = row.createCell(col++);

                        if (!filtered.isEmpty()) {
                            double avg = filtered.stream()
                                    .mapToDouble(v -> v.getNumberResponse() == null ? 0 : v.getNumberResponse())
                                    .average()
                                    .orElse(0);

                            valueCell.setCellValue(avg);
                        } else {
                            valueCell.setCellValue("");
                        }

                        valueCell.setCellStyle(valueStyle);
                    }

                    String text = responseList.stream()
                            .map(IndustryResponse::getTextResponse)
                            .filter(Objects::nonNull)
                            .filter(s -> !s.isBlank())
                            .findFirst()
                            .orElse("");

                    Cell textCell = row.createCell(col++);
                    textCell.setCellValue(text);
                    textCell.setCellStyle(wrapStyle);
                }

                // =====================
                // 3. 빈 줄
                // =====================
                rIdx++;

                // =====================
                // 4. 가중치 평가 영역
                // - 산업은 카테고리 3개라서 가중치 행 3개
                // =====================
                Row categoryRow = sheet.createRow(rIdx++);
                categoryRow.setHeight(rowHeight);

                Cell categoryTitleCell = categoryRow.createCell(0);
                categoryTitleCell.setCellValue("카테고리");
                categoryTitleCell.setCellStyle(weightHeaderStyle);

                String[] weightCategories = {
                        "심미성", "조형성", "독창성", "사용성",
                        "기능성", "윤리성", "경제성", "목적성"
                };

                for (int i = 0; i < weightCategories.length; i++) {
                    Cell cell = categoryRow.createCell(i + 1);
                    cell.setCellValue(weightCategories[i]);
                    cell.setCellStyle(weightHeaderStyle);
                }

                List<IndustryResponse> allResponsesForUser =
                        dataMap.values()
                                .stream()
                                .flatMap(List::stream)
                                .toList();

                Long userYearRoundId = allResponsesForUser.isEmpty()
                        ? null
                        : allResponsesForUser.get(0).getUserYearRound().getId();

                System.out.println(sheetName + " userYearRoundId = " + userYearRoundId);

                IndustryDataCategory[] industryCategories = {
                        IndustryDataCategory.HEADPHONE,
                        IndustryDataCategory.EARPHONE,
                        IndustryDataCategory.BLUETOOTH_SPEAKER
                };

                for (IndustryDataCategory category : industryCategories) {

                    Row weightRow = sheet.createRow(rIdx++);
                    weightRow.setHeight(rowHeight);

                    Cell weightTitleCell = weightRow.createCell(0);
                    weightTitleCell.setCellValue(getCategoryDisplayName(category));
                    weightTitleCell.setCellStyle(weightHeaderStyle);

                    IndustryWeightedScore weight = userYearRoundId == null
                            ? null
                            : weightMapByUserYearRoundIdAndCategory.get(
                            weightKey(userYearRoundId, category)
                    );

                    if (weight != null) {
                        createWeightValueCell(weightRow, 1, weight.getScore1(), weightValueStyle);
                        createWeightValueCell(weightRow, 2, weight.getScore2(), weightValueStyle);
                        createWeightValueCell(weightRow, 3, weight.getScore3(), weightValueStyle);
                        createWeightValueCell(weightRow, 4, weight.getScore4(), weightValueStyle);
                        createWeightValueCell(weightRow, 5, weight.getScore5(), weightValueStyle);
                        createWeightValueCell(weightRow, 6, weight.getScore6(), weightValueStyle);
                        createWeightValueCell(weightRow, 7, weight.getScore7(), weightValueStyle);
                        createWeightValueCell(weightRow, 8, weight.getScore8(), weightValueStyle);
                    } else {
                        System.out.println("⚠️ 산업 가중치 없음: "
                                + sheetName
                                + ", userYearRoundId="
                                + userYearRoundId
                                + ", category="
                                + category);

                        for (int i = 1; i <= 8; i++) {
                            Cell emptyCell = weightRow.createCell(i);
                            emptyCell.setCellValue("");
                            emptyCell.setCellStyle(weightValueStyle);
                        }
                    }
                }

                for (int i = 0; i < Math.max(sortedSurveys.size() + 2, 10); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("산업 엑셀 생성 실패", e);
        }
    }

    public void exportIndustryResultToFile(Long yearId) {

        byte[] bytes = exportIndustryResultByTeam(yearId);

        String path = System.getProperty("user.home")
                + "/Downloads/industry_result.xlsx";

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes);
            System.out.println("📁 저장 완료: " + path);
        } catch (Exception e) {
            throw new RuntimeException("산업 파일 저장 실패", e);
        }
    }

    private void createWeightValueCell(Row row, int col, Integer value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
    }

    private String getTeam(String name) {
        return switch (name) {
            case "백은경", "안성훈" -> "1";
            case "함수정", "류관준" -> "2";
            case "김병수", "허정은" -> "3";
            case "김재희", "최정민" -> "4";
            case "노재승", "이상연" -> "5";
            case "김태완", "김현용" -> "6";
            case "양성원", "이지환" -> "7";
            case "김세희", "양성철" -> "8";
            case "김성한", "이현일" -> "9";
            case "박희면", "양동환" -> "10";
            default -> null;
        };
    }

    private IndustryData findIndustryData(List<IndustryData> dataList, Long dataId) {
        return dataList.stream()
                .filter(d -> d.getId().equals(dataId))
                .findFirst()
                .orElse(null);
    }

    private int getCodeNumber(IndustryData data) {
        if (data == null) return Integer.MAX_VALUE;

        String code = getIndustryCode(data);

        if (code == null || code.isBlank()) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(code.trim());
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    private String getIndustryCode(IndustryData data) {
        if (data == null) return "";
        return data.getOriginalId();
    }

    private String weightKey(Long userYearRoundId, IndustryDataCategory category) {
        return userYearRoundId + "_" + category.name();
    }

    private String getCategoryDisplayName(IndustryDataCategory category) {
        return switch (category) {
            case HEADPHONE -> "HEADPHONE";
            case EARPHONE -> "EARPHONE";
            case BLUETOOTH_SPEAKER -> "BLUETOOTH_SPEAKER";
            default -> category.name();
        };
    }

    private int extractTeamNumber(String sheetName) {
        try {
            return Integer.parseInt(sheetName.split(" ")[1].split("\\.")[0]);
        } catch (Exception e) {
            return 999;
        }
    }

    private String extractUserNameFromSheetName(String sheetName) {
        try {
            return sheetName.split("\\.")[1];
        } catch (Exception e) {
            return sheetName;
        }
    }

    private String safeName(String name) {
        return name == null ? "미정" : name.trim();
    }

    private String safeSheetName(String name) {
        String safe = name
                .replace("/", "_")
                .replace("\\", "_")
                .replace("?", "_")
                .replace("*", "_")
                .replace("[", "_")
                .replace("]", "_")
                .replace(":", "_");

        return safe.length() > 31 ? safe.substring(0, 31) : safe;
    }

    private String nvl(Object v) {
        return v == null ? "" : v.toString();
    }
}