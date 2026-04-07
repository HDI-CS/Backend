package kr.co.hdi.domain.data.enums;

import kr.co.hdi.domain.data.entity.IndustryData;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.BiConsumer;

public enum IndustryExcelType {

    HEADPHONE(
            new String[]{
                    "ID",
                    "제품명",
                    "회사명",
                    "노이즈 캔슬링",
                    "코덱",
                    "부가기능",
                    "컨트롤 방식",
                    "최대 재생시간",
                    "충전 시간"
            },
            (row, i) -> {
                int c = 0;
                row.createCell(c++).setCellValue(nvl(i.getOriginalId()));
                row.createCell(c++).setCellValue(nvl(i.getProductName()));
                row.createCell(c++).setCellValue(nvl(i.getCompanyName()));
                row.createCell(c++).setCellValue(nvl(i.getNoiseCancelling()));
                row.createCell(c++).setCellValue(nvl(i.getCodec()));
                row.createCell(c++).setCellValue(nvl(i.getExtraFeatures()));
                row.createCell(c++).setCellValue(nvl(i.getControlType()));
                row.createCell(c++).setCellValue(nvl(i.getMaxPlayTime()));
                row.createCell(c++).setCellValue(nvl(i.getChargeTime()));
                row.createCell(c++).setCellValue(nvl(i.getReferenceUrl()));

            }
    ),

    EARPHONE(
            new String[]{
                    "ID",
                    "제품명",
                    "회사명",
                    "노이즈 캔슬링",
                    "코덱",
                    "부가기능",
                    "컨트롤 방식",
                    "최대 재생시간",
                    "충전 시간",
                    "방수 여부",
                    "무게",
                    "가격",
                    "등록일",
                    "쇼핑 URL",
                    "참고 URL"
            },
            (row, i) -> {
                int c = 0;
                row.createCell(c++).setCellValue(nvl(i.getOriginalId()));
                row.createCell(c++).setCellValue(nvl(i.getProductName()));
                row.createCell(c++).setCellValue(nvl(i.getCompanyName()));
                row.createCell(c++).setCellValue(nvl(i.getNoiseCancelling()));
                row.createCell(c++).setCellValue(nvl(i.getCodec()));
                row.createCell(c++).setCellValue(nvl(i.getExtraFeatures()));
                row.createCell(c++).setCellValue(nvl(i.getControlType()));
                row.createCell(c++).setCellValue(nvl(i.getMaxPlayTime()));
                row.createCell(c++).setCellValue(nvl(i.getChargeTime()));

                row.createCell(c++).setCellValue(nvl(i.getWaterproof()));
                row.createCell(c++).setCellValue(nvl(i.getWeight()));
                row.createCell(c++).setCellValue(nvl(i.getPrice()));
                row.createCell(c++).setCellValue(nvl(i.getRegisteredAt()));
                row.createCell(c++).setCellValue(nvl(i.getShoppingUrl()));
                row.createCell(c++).setCellValue(nvl(i.getReferenceUrl()));
            }
    ),
    BLUETOOTH_SPEAKER(
            new String[]{
                    "ID",
                    "회사명",
                    "모델명",
                    "카테고리",
                    "유형",
                    "용도",
                    "사운드 출력",
                    "코덱",
                    "부가기능",
                    "입출력",
                    "최대 재생시간(hr)",
                    "1회 충전시간(hr)",
                    "무게(g)",
                    "출시 가격(원)",
                    "출시 날짜",
                    "판매 웹페이지"
            },
            (row, i) -> {
                int c = 0;
                row.createCell(c++).setCellValue(nvl(i.getOriginalId()));
                row.createCell(c++).setCellValue(nvl(i.getCompanyName()));
                row.createCell(c++).setCellValue(nvl(i.getProductName()));
                row.createCell(c++).setCellValue(nvl(i.getProductPath()));
                row.createCell(c++).setCellValue(nvl(i.getProductTypeName()));
                row.createCell(c++).setCellValue(nvl(i.getUsage()));

                row.createCell(c++).setCellValue(nvl(i.getSoundOutput()));
                row.createCell(c++).setCellValue(nvl(i.getCodec()));
                row.createCell(c++).setCellValue(nvl(i.getExtraFeatures()));
                row.createCell(c++).setCellValue(nvl(i.getConnectivity()));

                row.createCell(c++).setCellValue(nvl(i.getMaxPlayTime()));
                row.createCell(c++).setCellValue(nvl(i.getChargeTime()));
                row.createCell(c++).setCellValue(nvl(i.getWeight()));
                row.createCell(c++).setCellValue(nvl(i.getPrice()));
                row.createCell(c++).setCellValue(nvl(i.getRegisteredAt()));
                row.createCell(c++).setCellValue(nvl(i.getReferenceUrl()));
            }
    ),
    DEFAULT(
            new String[]{
                    "ID",
                    "Product Name",
                    "Model Name",
                    "Price",
                    "Material",
                    "Size",
                    "Weight",
                    "Reference URL",
                    "Registered At",
                    "Product Path",
                    "Product Type Name"
            },
            (row, i) -> {
                int c = 0;
                row.createCell(c++).setCellValue(nvl(i.getProductName()));
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
    );


    private final String[] headers;
    private final BiConsumer<Row, IndustryData> writer;

    IndustryExcelType(String[] headers, BiConsumer<Row, IndustryData> writer) {
        this.headers = headers;
        this.writer = writer;
    }

    public String[] getHeaders() {
        return headers;
    }

    public BiConsumer<Row, IndustryData> getWriter() {
        return writer;
    }

    // null-safe 처리
    private static String nvl(Object v) {
        return v == null ? "" : v.toString();
    }
}
