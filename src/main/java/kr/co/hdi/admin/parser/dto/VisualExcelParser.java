package kr.co.hdi.admin.parser.dto;



import kr.co.hdi.admin.parser.dto.VisualImportRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class VisualExcelParser {

    private static final Map<String, String> HEADER_MAP = Map.ofEntries(
            Map.entry("ID", "code"),
            Map.entry("부문·카테고리", "sectorCategory"),
            Map.entry("제목", "title"),
            Map.entry("년도", "releaseYear"),
            Map.entry("국가", "country"),
            Map.entry("클라이언트", "clientName"),
            Map.entry("내용 유형", "contentType"),
            Map.entry("시각 유형", "visualType"),
            Map.entry("디자인 설명", "designDescription"),
            Map.entry("평가 이미지", "originalLogoImage"),
            Map.entry("원본 링크", "referenceUrl")
    );

    public List<VisualImportRow> parse(Path excelPath) {

        try (InputStream in = Files.newInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = sheet.getRow(0);

            Map<Integer, String> columnMap = new HashMap<>();

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = get(headerRow, i);
                String field = HEADER_MAP.get(header);

                if (field != null) {
                    columnMap.put(i, field);
                }
            }

            List<VisualImportRow> result = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> map = new HashMap<>();

                for (Map.Entry<Integer, String> e : columnMap.entrySet()) {
                    map.put(e.getValue(), get(row, e.getKey()));
                }

                result.add(
                        VisualImportRow.builder()
                                .code(map.get("code"))
                                .sectorCategory(map.get("sectorCategory"))
                                .title(map.get("title"))
                                .releaseYear(map.get("releaseYear"))
                                .country(map.get("country"))
                                .clientName(map.get("clientName"))
                                .contentType(map.get("contentType"))
                                .visualType(map.get("visualType"))
                                .designDescription(map.get("designDescription"))
                                .originalLogoImage(map.get("originalLogoImage"))
                                .referenceUrl(map.get("referenceUrl"))
                                .build()
                );
            }

            return result;

        } catch (Exception e) {
            throw new IllegalStateException("엑셀 파싱 실패", e);
        }
    }

    private String get(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

}