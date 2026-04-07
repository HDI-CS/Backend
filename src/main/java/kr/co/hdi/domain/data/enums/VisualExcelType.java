package kr.co.hdi.domain.data.enums;
import kr.co.hdi.domain.data.entity.VisualData;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.BiConsumer;

public enum VisualExcelType {

    POSTER(
            new String[]{
                    "ID",
                    "섹터 카테고리",
                    "제목",
                    "국가",
                    "클라이언트",
                    "콘텐츠 유형",
                    "시각 유형",
                    "디자인 설명",
                    "출시년도",

                    "카테고리",
                    "참고 URL",
            },
            (row, i) -> {
                int c = 0;

                row.createCell(c++).setCellValue(nvl(i.getId()));
                row.createCell(c++).setCellValue(nvl(i.getSectorCategory()));

                row.createCell(c++).setCellValue(nvl(i.getTitle()));
                row.createCell(c++).setCellValue(nvl(i.getCountry()));
                row.createCell(c++).setCellValue(nvl(i.getClientName()));
                row.createCell(c++).setCellValue(nvl(i.getContentType()));
                row.createCell(c++).setCellValue(nvl(i.getVisualType()));
                row.createCell(c++).setCellValue(nvl(i.getDesignDescription()));
                row.createCell(c++).setCellValue(nvl(i.getReleaseYear()));

                row.createCell(c++).setCellValue(nvl(i.getVisualDataCategory()));
                row.createCell(c++).setCellValue(nvl(i.getReferenceUrl()));

            }

    ),
    DEFAULT(
            new String[]{
                    "ID",
                    "브랜드 코드",
                    "브랜드명",
                    "섹터 카테고리",
                    "메인 제품 카테고리",
                    "메인 제품",
                    "타겟",
                    "참고 URL",


                    "카테고리"
            },
            (row, i) -> {
                int c = 0;

                row.createCell(c++).setCellValue(nvl(i.getId()));
                row.createCell(c++).setCellValue(nvl(i.getBrandCode()));
                row.createCell(c++).setCellValue(nvl(i.getBrandName()));
                row.createCell(c++).setCellValue(nvl(i.getSectorCategory()));
                row.createCell(c++).setCellValue(nvl(i.getMainProductCategory()));
                row.createCell(c++).setCellValue(nvl(i.getMainProduct()));
                row.createCell(c++).setCellValue(nvl(i.getTarget()));
                row.createCell(c++).setCellValue(nvl(i.getReferenceUrl()));


                row.createCell(c++).setCellValue(nvl(i.getVisualDataCategory()));
            }
    );

    private final String[] headers;
    private final BiConsumer<Row, VisualData> writer;

    VisualExcelType(String[] headers, BiConsumer<Row, VisualData> writer) {
        this.headers = headers;
        this.writer = writer;
    }

    public String[] getHeaders() {
        return headers;
    }

    public BiConsumer<Row, VisualData> getWriter() {
        return writer;
    }

    private static String nvl(Object v) {
        return v == null ? "" : v.toString();
    }
}