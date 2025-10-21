package kr.co.hdi.result.service;

import kr.co.hdi.survey.domain.BrandResponse;
import kr.co.hdi.survey.domain.ProductResponse;
import kr.co.hdi.survey.domain.WeightedScore;
import kr.co.hdi.survey.repository.BrandResponseRepository;
import kr.co.hdi.survey.repository.ProductResponseRepository;
import kr.co.hdi.survey.repository.WeightedScoreRepository;
import kr.co.hdi.user.domain.UserEntity;
import kr.co.hdi.user.exception.AuthErrorCode;
import kr.co.hdi.user.exception.AuthException;
import kr.co.hdi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {

    private final UserRepository userRepository;
    private final WeightedScoreRepository weightedScoreRepository;
    private final BrandResponseRepository brandResponseRepository;
    private final ProductResponseRepository productResponseRepository;


    // 결과 뽑기
    public void getResponseResult(String type, String path) {

        String fileName = path + "/result_" + LocalDate.now() + ".xlsx";

        if (type.equals("BRAND")) {

            List<BrandResponse> brandResponses = brandResponseRepository.findAll();
            try {
                exportBrandResultPerUser(brandResponses, fileName);
            } catch (IOException e) {
                log.error("브랜드 응답 CSV 내보내기 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        if (type.equals("PRODUCT")) {

            List<ProductResponse> productResponses = productResponseRepository.findAll();
            try {
                exportProductResultPerUser(productResponses, fileName);
            } catch (IOException e) {
                log.error("제품 응답 CSV 내보내기 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

    public void exportBrandResultPerUser(List<BrandResponse> responses, String outputPath) throws IOException {

        // 유저별로 BrandResponse 그룹화
        Map<Long, List<BrandResponse>> groupedByUser = responses.stream()
                .collect(Collectors.groupingBy(br -> br.getUser().getId()));

        // brand id 기준 오름차순 정렬
        groupedByUser.values().forEach(list ->
                list.sort(Comparator.comparing(br -> br.getBrand().getId()))
        );

        Workbook workbook = new XSSFWorkbook();

        for (Map.Entry<Long, List<BrandResponse>> entry : groupedByUser.entrySet()) {
            Long userId = entry.getKey();
            List<BrandResponse> userResponses = entry.getValue();

            if (userId == 3L || userId == 9L) // 테스트 계정 제외
                continue;

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

            // 유저 이름으로 시트 생성 (엑셀 시트명 31자 제한, 특수문자 처리)
            String sheetName = sanitizeSheetName(user.getName());
            Sheet sheet = workbook.createSheet(sheetName);

            int rowIndex = 0;

            // 응답 헤더
            Row headerRow = sheet.createRow(rowIndex++);
            makeBrandHeaderRow(headerRow, "brandId");

            // 응답 데이터 작성
            for (BrandResponse br : userResponses) {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;

                row.createCell(cellIndex++).setCellValue(br.getBrand().getBrandCode());

                List<Integer> numResponse = br.getResponses();
                for (Integer r : numResponse) {
                    row.createCell(cellIndex++).setCellValue(r != null ? r : 0);
                }

                String text = br.getTextResponse() != null ? br.getTextResponse() : "";
                row.createCell(cellIndex).setCellValue(text);
            }
            rowIndex += 2;

            // 가중치 평가 헤더
            Row wsHeader = sheet.createRow(rowIndex++);
            String[] wsHeaderNames = {"카테고리", "심미성", "조형성", "독창성", "사용성", "기능성", "윤리성", "경제성", "목적성"};
            for (int i = 0; i < wsHeaderNames.length; i++) {
                wsHeader.createCell(i).setCellValue(wsHeaderNames[i]);
            }

            // 가중치 평가 데이터
            List<WeightedScore> weightedScores = weightedScoreRepository.findByUser(user);
            for (WeightedScore ws : weightedScores) {
                Row row = sheet.createRow(rowIndex++);
                buildWeightedScoreRow(row, ws);
            }

            // 자동 컬럼 너비
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Excel 파일 저장
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }


    public void exportProductResultPerUser(List<ProductResponse> responses, String outputPath) throws IOException {

        // 유저별 ProductResponse 그룹화
        Map<Long, List<ProductResponse>> groupedByUser = responses.stream()
                .collect(Collectors.groupingBy(br -> br.getUser().getId()));

        // product id 기준으로 오름차순 정렬
        groupedByUser.values().forEach(list ->
                list.sort(Comparator.comparing(pr -> pr.getProduct().getOriginalId()))
        );

        // 하나의 Excel Workbook 생성
        Workbook workbook = new XSSFWorkbook();

        for (Map.Entry<Long, List<ProductResponse>> entry : groupedByUser.entrySet()) {
            Long userId = entry.getKey();
            List<ProductResponse> userResponses = entry.getValue();

            if (userId == 2L || userId == 8L) // 테스트 계정 제외
                continue;

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

            // 유저 이름으로 시트 생성 (엑셀 시트명은 31자 제한, 특수문자 제한 주의)
            String sheetName = sanitizeSheetName(user.getName());
            Sheet sheet = workbook.createSheet(sheetName);

            int rowIndex = 0;

            // 산디 헤더 작성
            Row headerRow = sheet.createRow(rowIndex++);
            makeProductHeaderRow(headerRow, "productId");

            // 응답 데이터 작성
            for (ProductResponse pr : userResponses) {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;

                row.createCell(cellIndex++).setCellValue(pr.getProduct().getOriginalId());

                List<Integer> numResponse = pr.getResponses();
                for (Integer r : numResponse) {
                    row.createCell(cellIndex++).setCellValue(r != null ? r : 0);
                }

                String text = pr.getTextResponse() != null ? pr.getTextResponse() : "";
                row.createCell(cellIndex).setCellValue(text);
            }
            rowIndex += 2;

            // 가중치 평가 헤더
            Row wsHeader = sheet.createRow(rowIndex++);
            String[] wsHeaderNames = {"카테고리", "심미성", "조형성", "독창성", "사용성", "기능성", "윤리성", "경제성", "목적성"};
            for (int i = 0; i < wsHeaderNames.length; i++) {
                wsHeader.createCell(i).setCellValue(wsHeaderNames[i]);
            }

            // 가중치 평가 데이터
            List<WeightedScore> weightedScores = weightedScoreRepository.findByUser(user);
            for (WeightedScore ws : weightedScores) {
                Row row = sheet.createRow(rowIndex++);
                buildWeightedScoreRow(row, ws);
            }

            // 자동 컬럼 너비 조정
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Excel 파일 저장
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    private void makeBrandHeaderRow(Row headerRow, String id) {
        int cellIndex = 0;
        headerRow.createCell(cellIndex++).setCellValue(id);

        headerRow.createCell(cellIndex++).setCellValue("VI_AES_IM");
        headerRow.createCell(cellIndex++).setCellValue("VI_AES_AT");
        headerRow.createCell(cellIndex++).setCellValue("VI_AES_FS");
        headerRow.createCell(cellIndex++).setCellValue("VI_AES_CO");
        headerRow.createCell(cellIndex++).setCellValue("VI_FRM_PR");
        headerRow.createCell(cellIndex++).setCellValue("VI_FRM_BL");
        headerRow.createCell(cellIndex++).setCellValue("VI_FRM_CN");
        headerRow.createCell(cellIndex++).setCellValue("VI_FRM_UN");
        headerRow.createCell(cellIndex++).setCellValue("VI_FRM_HM");
        headerRow.createCell(cellIndex++).setCellValue("VI_CRE_IM");
        headerRow.createCell(cellIndex++).setCellValue("VI_CRE_CR");
        headerRow.createCell(cellIndex++).setCellValue("VI_CRE_UN");
        headerRow.createCell(cellIndex++).setCellValue("VI_USB_AP");
        headerRow.createCell(cellIndex++).setCellValue("VI_USB_FL");
        headerRow.createCell(cellIndex++).setCellValue("VI_FNC_RC");
        headerRow.createCell(cellIndex++).setCellValue("VI_FNC_IN");
        headerRow.createCell(cellIndex++).setCellValue("VI_FNC_RD");
        headerRow.createCell(cellIndex++).setCellValue("VI_ETH_SO");
        headerRow.createCell(cellIndex++).setCellValue("VI_ETH_CU");
        headerRow.createCell(cellIndex++).setCellValue("VI_ETH_LE");
        headerRow.createCell(cellIndex++).setCellValue("VI_ECN_CS");
        headerRow.createCell(cellIndex++).setCellValue("VI_ECN_PR");
        headerRow.createCell(cellIndex++).setCellValue("VI_ECN_EF");
        headerRow.createCell(cellIndex++).setCellValue("VI_PRP_CL");
        headerRow.createCell(cellIndex++).setCellValue("VI_PRP_AP");
        headerRow.createCell(cellIndex++).setCellValue("VI_OVE_PQ_1");
        headerRow.createCell(cellIndex++).setCellValue("VI_OVE_PQ_2");
        headerRow.createCell(cellIndex++).setCellValue("VI_OVE_PQ_3");
        headerRow.createCell(cellIndex++).setCellValue("VI_OVE_PS_1");
        headerRow.createCell(cellIndex++).setCellValue("VI_OVE_PS_2");

        headerRow.createCell(cellIndex).setCellValue("textResponse");
    }

    private void makeProductHeaderRow(Row headerRow, String id) {
        int cellIndex = 0;
        headerRow.createCell(cellIndex++).setCellValue(id);

        headerRow.createCell(cellIndex++).setCellValue("PR_AES_HC");
        headerRow.createCell(cellIndex++).setCellValue("PR_AES_HM");
        headerRow.createCell(cellIndex++).setCellValue("PR_AES_FI");
        headerRow.createCell(cellIndex++).setCellValue("PR_AES_AW_1");
        headerRow.createCell(cellIndex++).setCellValue("PR_AES_AW_2");
        headerRow.createCell(cellIndex++).setCellValue("PR_AES_AW_3");
        headerRow.createCell(cellIndex++).setCellValue("PR_FRM_QT");
        headerRow.createCell(cellIndex++).setCellValue("PR_FRM_PR");
        headerRow.createCell(cellIndex++).setCellValue("PR_FRM_CN");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_TY");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_AES_1");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_AES_2");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_AES_3");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_FN");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_AW_1");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_AW_2");
        headerRow.createCell(cellIndex++).setCellValue("PR_ORI_AW_3");
        headerRow.createCell(cellIndex++).setCellValue("PR_USB_LN");
        headerRow.createCell(cellIndex++).setCellValue("PR_USB_OP");
        headerRow.createCell(cellIndex++).setCellValue("PR_USB_ST");
        headerRow.createCell(cellIndex++).setCellValue("PR_USB_AW_1");
        headerRow.createCell(cellIndex++).setCellValue("PR_USB_AW_2");
        headerRow.createCell(cellIndex++).setCellValue("PR_USB_AW_3");
        headerRow.createCell(cellIndex++).setCellValue("PR_FNC_ST");
        headerRow.createCell(cellIndex++).setCellValue("PR_FNC_CT");
        headerRow.createCell(cellIndex++).setCellValue("PR_FNC_AP");
        headerRow.createCell(cellIndex++).setCellValue("PR_ETH_SO");
        headerRow.createCell(cellIndex++).setCellValue("PR_ETH_SM");
        headerRow.createCell(cellIndex++).setCellValue("PR_ETH_EN");
        headerRow.createCell(cellIndex++).setCellValue("PR_ETH_CM");
        headerRow.createCell(cellIndex++).setCellValue("PR_ECN_PR");
        headerRow.createCell(cellIndex++).setCellValue("PR_ECN_DR");
        headerRow.createCell(cellIndex++).setCellValue("PR_ECN_MT");
        headerRow.createCell(cellIndex++).setCellValue("PR_PRP_US");
        headerRow.createCell(cellIndex++).setCellValue("PR_PRP_IN");
        headerRow.createCell(cellIndex++).setCellValue("PR_PRP_SM");
        headerRow.createCell(cellIndex++).setCellValue("PR_OVE_PQ_1");
        headerRow.createCell(cellIndex++).setCellValue("PR_OVE_PQ_2");
        headerRow.createCell(cellIndex++).setCellValue("PR_OVE_PQ_3");
        headerRow.createCell(cellIndex++).setCellValue("PR_OVE_PS_1");
        headerRow.createCell(cellIndex++).setCellValue("PR_OVE_PS_2");

        headerRow.createCell(cellIndex).setCellValue("textResponse");
    }

    private void buildWeightedScoreRow(Row row, WeightedScore ws) {
        int cellIndex = 0;
        row.createCell(cellIndex++).setCellValue(ws.getCategory().toString());
        row.createCell(cellIndex++).setCellValue(ws.getScore1());
        row.createCell(cellIndex++).setCellValue(ws.getScore2());
        row.createCell(cellIndex++).setCellValue(ws.getScore3());
        row.createCell(cellIndex++).setCellValue(ws.getScore4());
        row.createCell(cellIndex++).setCellValue(ws.getScore5());
        row.createCell(cellIndex++).setCellValue(ws.getScore6());
        row.createCell(cellIndex++).setCellValue(ws.getScore7());
        row.createCell(cellIndex).setCellValue(ws.getScore8());
    }

    /**
     * 엑셀 시트 이름은 31자 이하, 특수문자 제한
     */
    private String sanitizeSheetName(String name) {
        String sanitized = name.replaceAll("[\\\\/?*\\[\\]:]", "_");
        return sanitized.length() > 31 ? sanitized.substring(0, 31) : sanitized;
    }
}
