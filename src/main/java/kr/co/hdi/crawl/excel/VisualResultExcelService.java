package kr.co.hdi.crawl.excel;

import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.response.entity.VisualResponse;
import kr.co.hdi.domain.response.entity.VisualWeightedScore;
import kr.co.hdi.domain.response.repository.VisualResponseRepository;
import kr.co.hdi.domain.response.repository.VisualWeightedScoreRepository;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import kr.co.hdi.domain.survey.repository.VisualSurveyRepository;
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
public class VisualResultExcelService {

    private final VisualDataRepository visualDataRepository;
    private final VisualSurveyRepository visualSurveyRepository;
    private final VisualResponseRepository visualResponseRepository;
    private final VisualWeightedScoreRepository visualWeightedScoreRepository;

    public byte[] exportVisualResultByTeam(Long yearId) {

        List<VisualData> dataList =
                visualDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        List<VisualSurvey> surveys =
                visualSurveyRepository.findAllByYear(yearId);

        List<VisualResponse> responses =
                visualResponseRepository.findAllByYearId(yearId);

        List<VisualWeightedScore> weights =
                visualWeightedScoreRepository.findAllByYearId(yearId);

        Map<Long, VisualWeightedScore> weightMapByUserYearRoundId =
                weights.stream()
                        .collect(Collectors.toMap(
                                w -> w.getUserYearRound().getId(),
                                w -> w,
                                (w1, w2) -> w1
                        ));

        System.out.println("가중치 개수 = " + weights.size());
        System.out.println("가중치 userYearRoundIds = " + weightMapByUserYearRoundId.keySet());

        List<String> surveyCodes = surveys.stream()
                .map(VisualSurvey::getSurveyCode)
                .distinct()
                .sorted()
                .toList();

        Map<String, Map<Long, List<VisualResponse>>> teamDataMap =
                responses.stream()
                        .collect(Collectors.groupingBy(
                                r -> {
                                    String name = safeName(r.getUserYearRound().getUser().getName());
                                    String team = getTeam(name);
                                    return "Team " + team + "." + name;
                                },
                                Collectors.groupingBy(
                                        r -> r.getVisualData().getId()
                                )
                        ));

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            short rowHeight = 400;

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle wrapStyle = wb.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle weightHeaderStyle = wb.createCellStyle();
            Font weightFont = wb.createFont();
            weightFont.setBold(true);
            weightHeaderStyle.setFont(weightFont);
            weightHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            weightHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

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

                for (String code : surveyCodes) {
                    Cell cell = header.createCell(c++);
                    cell.setCellValue(code);
                    cell.setCellStyle(headerStyle);
                }

                Cell textHeaderCell = header.createCell(c++);
                textHeaderCell.setCellValue("정성평가");
                textHeaderCell.setCellStyle(headerStyle);

                // =====================
                // 2. 설문 평가 데이터
                // =====================
                Map<Long, List<VisualResponse>> dataMap = teamDataMap.get(sheetName);

                List<Long> sortedDataIds = dataMap.keySet().stream()
                        .sorted(Comparator.comparing(dataId -> {
                            VisualData data = findVisualData(dataList, dataId);
                            return getCodeNumber(data);
                        }))
                        .toList();

                for (Long dataId : sortedDataIds) {

                    VisualData data = findVisualData(dataList, dataId);
                    if (data == null) continue;

                    Row row = sheet.createRow(rIdx++);
                    row.setHeight(rowHeight);

                    int col = 0;

                    row.createCell(col++).setCellValue(nvl(data.getBrandCode()));

                    List<VisualResponse> responseList = dataMap.get(dataId);

                    for (String code : surveyCodes) {

                        List<VisualResponse> filtered = responseList.stream()
                                .filter(v -> v.getVisualSurvey() != null)
                                .filter(v -> code.equals(v.getVisualSurvey().getSurveyCode()))
                                .toList();

                        if (!filtered.isEmpty()) {
                            double avg = filtered.stream()
                                    .mapToDouble(v -> v.getNumberResponse() == null ? 0 : v.getNumberResponse())
                                    .average()
                                    .orElse(0);

                            row.createCell(col++).setCellValue(avg);
                        } else {
                            row.createCell(col++).setCellValue("");
                        }
                    }

                    String text = responseList.stream()
                            .map(VisualResponse::getTextResponse)
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

                Row weightRow = sheet.createRow(rIdx++);
                weightRow.setHeight(rowHeight);

                Cell weightTitleCell = weightRow.createCell(0);
                weightTitleCell.setCellValue("가중치 평가");
                weightTitleCell.setCellStyle(weightHeaderStyle);

                List<VisualResponse> allResponsesForUser =
                        dataMap.values()
                                .stream()
                                .flatMap(List::stream)
                                .toList();

                Long userYearRoundId = allResponsesForUser.isEmpty()
                        ? null
                        : allResponsesForUser.get(0).getUserYearRound().getId();

                System.out.println(sheetName + " userYearRoundId = " + userYearRoundId);

                VisualWeightedScore weight = userYearRoundId == null
                        ? null
                        : weightMapByUserYearRoundId.get(userYearRoundId);

                if (weight != null) {
                    weightRow.createCell(1).setCellValue(weight.getScore1());
                    weightRow.createCell(2).setCellValue(weight.getScore2());
                    weightRow.createCell(3).setCellValue(weight.getScore3());
                    weightRow.createCell(4).setCellValue(weight.getScore4());
                    weightRow.createCell(5).setCellValue(weight.getScore5());
                    weightRow.createCell(6).setCellValue(weight.getScore6());
                    weightRow.createCell(7).setCellValue(weight.getScore7());
                    weightRow.createCell(8).setCellValue(weight.getScore8());
                } else {
                    System.out.println("⚠️ 가중치 없음: " + sheetName + ", userYearRoundId=" + userYearRoundId);
                }

                for (int i = 0; i < Math.max(surveyCodes.size() + 2, 10); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("엑셀 생성 실패", e);
        }
    }

    public void exportVisualResultToFile(Long yearId) {

        byte[] bytes = exportVisualResultByTeam(yearId);

        String path = System.getProperty("user.home")
                + "/Downloads/visual_result.xlsx";

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes);
            System.out.println("📁 저장 완료: " + path);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private String getTeam(String name) {
        return switch (name) {
            case "석재원", "최슬기" -> "1";
            case "미정", "저스틴고" -> "2";
            case "이규락", "고은영" -> "3";
            case "김보영", "송민승" -> "4";
            case "윤영민", "신자영" -> "5";
            case "하상목", "장미네" -> "6";
            case "이주원", "김보라" -> "7";
            case "전재환", "박우경" -> "8";
            case "강주현", "정사록" -> "9";
            case "채병록" -> "10";
            default -> "미정";
        };
    }

    private VisualData findVisualData(List<VisualData> dataList, Long dataId) {
        return dataList.stream()
                .filter(d -> d.getId().equals(dataId))
                .findFirst()
                .orElse(null);
    }

    private int getCodeNumber(VisualData data) {
        if (data == null) return Integer.MAX_VALUE;

        String code = data.getBrandCode();

        if (code == null || code.isBlank()) {
            code = data.getBrandCode();
        }

        if (code == null || code.isBlank()) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(code.trim());
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
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