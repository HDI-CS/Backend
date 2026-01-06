package kr.co.hdi.admin.evaluation.service;

import kr.co.hdi.admin.evaluation.dto.enums.EvaluationType;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationAnswerByDataResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationAnswerByMemberResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusByMemberResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusResponse;
import kr.co.hdi.admin.evaluation.exeption.EvaluationErrorCode;
import kr.co.hdi.admin.evaluation.exeption.EvaluationException;
import kr.co.hdi.domain.assignment.query.DataIdCodePair;
import kr.co.hdi.domain.assignment.query.UserDataIdCodePair;
import kr.co.hdi.domain.assignment.query.UserDataPair;
import kr.co.hdi.domain.assignment.repository.IndustryDataAssignmentRepository;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.response.entity.IndustryWeightedScore;
import kr.co.hdi.domain.response.repository.IndustryResponseRepository;
import kr.co.hdi.domain.response.repository.IndustryWeightedScoreRepository;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.repository.IndustrySurveyRepository;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.user.repository.UserRepository;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
import kr.co.hdi.domain.year.repository.UserYearRoundRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndustryEvaluationService implements EvaluationService {

    private final UserRepository userRepository;
    private final IndustryResponseRepository industryResponseRepository;
    private final IndustryWeightedScoreRepository industryWeightedScoreRepository;
    private final IndustryDataAssignmentRepository industryDataAssignmentRepository;
    private final AssessmentRoundRepository assessmentRoundRepository;
    private final IndustrySurveyRepository industrySurveyRepository;
    private final UserYearRoundRepository userYearRoundRepository;

    @Override
    public DomainType getDomainType() {
        return DomainType.INDUSTRY;
    }

    /*
    평가 응답 전체 조회
    0. userType 및 surveyCount 할당
    1. 데이터 조회 (전문가, 정성평가 응답, 산업디자인 가중치평가 응답, 할당된 평가데이터)
    2. 전문가별 응답문항/데이터 그룹핑
    3. 응답 생성 (createEvaluationStatus 사용)
     */
    @Override
    public List<EvaluationStatusByMemberResponse> getEvaluationStatus(
            DomainType type,
            Long assessmentRoundId,
            String q
    ) {

        // 평가 회차 조회 및 검증 (Year Fetch Join으로 <surveyCount>에서 추가 쿼리 방지)
        AssessmentRound assessmentRound = assessmentRoundRepository
                .findByIdWithYear(assessmentRoundId)
                .orElseThrow(() -> new EvaluationException(
                        EvaluationErrorCode.ASSESSMENT_ROUND_NOT_FOUND));

        UserType userType = type.toUserType();
        Integer surveyCount = assessmentRound.getYear().getSurveyCount();

        List<UserEntity> users =
                (q == null || q.isBlank())
                        ? userYearRoundRepository.findUsers(userType, assessmentRound)
                        : userYearRoundRepository.findUsersBySearch(userType, assessmentRound, q);

        List<UserDataPair> dataAssignments = industryDataAssignmentRepository.findUserDataPairsByAssessmentRoundId(assessmentRoundId);
        List<IndustryWeightedScore> weightedScores = industryWeightedScoreRepository.findAllByUserYearRound(assessmentRoundId);
        List<IndustryResponse> qualitativeResponses = industryResponseRepository.findAllByUserYearRound(assessmentRoundId);

        // 전문가-할당데이터 그룹핑 (모든 데이터)
        Map<Long, List<Long>> dataIdsByUserId =
                dataAssignments.stream().collect(groupingBy(
                        UserDataPair::userId,
                        mapping(UserDataPair::dataId, toList())
                ));

        // 전문가-할당데이터-정량평가응답 그룹핑 (응답한 데이터)
        Map<Long, Map<Long, List<IndustryResponse>>> responsesByUser =
                qualitativeResponses.stream()
                        .collect(groupingBy(
                                r -> r.getUserYearRound().getUser().getId(),
                                groupingBy(r -> r.getIndustryData().getId())
                        ));

        // 전문가-가중치평가응답
        Map<Long, IndustryWeightedScore> weightedByUserId =
                weightedScores.stream()
                        .collect(Collectors.toMap(
                                w -> w.getUserYearRound().getUser().getId(),
                                w -> w
                        ));

        return users.stream()
                .map(user -> createEvaluationStatus(
                        user,
                        responsesByUser.getOrDefault(user.getId(), Map.of()),
                        weightedByUserId.get(user.getId()),
                        dataIdsByUserId.getOrDefault(user.getId(), List.of()),
                        surveyCount
                ))
                .toList();
    }

    /*
    평가 응답 전체 조회 응답 생성 헬퍼
    1. 데이터 별 정량평가 상태 확인
    2. 전문가 가중치평가 상태 확인
    3. 응답 생성
     */
    private EvaluationStatusByMemberResponse createEvaluationStatus(
            UserEntity user,
            Map<Long, List<IndustryResponse>> userResponses,
            IndustryWeightedScore weightedScore,
            List<Long> userDataIds,
            Integer surveyCount
    ) {
        // 데이터 id 오름차순으로 완료/미완료 상태를 담는 list
        List<EvaluationStatusResponse> statuses = userDataIds.stream()
                .sorted()
                .map(dataId -> {
                    List<IndustryResponse> list = userResponses.get(dataId);
                    boolean isDone = isQualitativeDone(list, surveyCount);
                    return EvaluationStatusResponse.of(EvaluationType.QUALITATIVE, isDone);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        statuses.add(EvaluationStatusResponse.of(EvaluationType.WEIGHTED, isWeightedDone(weightedScore)));

        return EvaluationStatusByMemberResponse.of(user, statuses);
    }

    /*
    정성 평가 상태 확인 헬퍼
     */
    private boolean isQualitativeDone(List<IndustryResponse> list, Integer surveyCount) {
        if (list == null || list.size() != surveyCount) {
            return false;
        }

        return list.stream().allMatch(r ->
                r.getNumberResponse() != null ||
                        (r.getTextResponse() != null && !r.getTextResponse().isBlank())
        );
    }

    /*
    가중치 평가 상태 확인 헬퍼
     */
    private boolean isWeightedDone(IndustryWeightedScore ws) {
        if (ws == null) return false;

        int total =
                nz(ws.getScore1()) + nz(ws.getScore2()) + nz(ws.getScore3()) + nz(ws.getScore4()) + nz(ws.getScore5()) + nz(ws.getScore6()) + nz(ws.getScore7()) + nz(ws.getScore8());

        return total == 100;
    }

    private int nz(Integer v) {
        return v == null ? 0 : v;
    }

    /*
    특정 전문가 응답 전체 조회
     */
    @Override
    public EvaluationAnswerByMemberResponse getEvaluationByMember(
            DomainType type,
            Long assessmentRoundId,
            Long memberId
    ) {

        // 평가 회차 조회 및 검증 (Year Fetch Join으로 <surveyCount>에서 추가 쿼리 방지)
        AssessmentRound assessmentRound = assessmentRoundRepository
                .findByIdWithYear(assessmentRoundId)
                .orElseThrow(() -> new EvaluationException(
                        EvaluationErrorCode.ASSESSMENT_ROUND_NOT_FOUND));

        UserType userType = type.toUserType();
        UserEntity user = userRepository.findByIdAndUserTypeAndDeletedAtIsNull(memberId, userType)
                .orElseThrow(() -> new EvaluationException(EvaluationErrorCode.USER_NOT_FOUND));

        List<DataIdCodePair> pairs = industryDataAssignmentRepository.findDataIdCodePairsByAssessmentRoundIdAndUserId(assessmentRoundId, memberId);
        List<IndustrySurvey> surveys = industrySurveyRepository.findAllByYear(assessmentRound.getYear().getId());
        List<IndustryResponse> responses = industryResponseRepository.findAllByAssessmentRoundIdAndMemberId(assessmentRoundId, memberId);

        Map<Long, List<IndustryResponse>> responsesByDataId = responses.stream()
                .filter(r -> r.getIndustryData() != null)
                .collect(Collectors.groupingBy(r -> r.getIndustryData().getId()));

        List<EvaluationAnswerByDataResponse> surveyDatas = pairs.stream()
                .map(pair -> EvaluationAnswerByDataResponse.ofIndustry(
                        pair,
                        surveys,
                        responsesByDataId.getOrDefault(pair.dataId(), List.of())
                ))
                .toList();

        return EvaluationAnswerByMemberResponse.of(user, surveyDatas);
    }

    /*
    평가 응답 데이터셋 액셀 다운로드
     */
    @Override
    public byte[] exportEvaluationExcelsZip(
            DomainType type,
            Long assessmentRoundId
    ) {
        AssessmentRound assessmentRound = assessmentRoundRepository
                .findByIdWithYear(assessmentRoundId)
                .orElseThrow(() -> new EvaluationException(EvaluationErrorCode.ASSESSMENT_ROUND_NOT_FOUND));

        UserType userType = type.toUserType();
        Integer surveyCount = Optional.ofNullable(assessmentRound.getYear().getSurveyCount()).orElse(0);

        List<UserEntity> users = userYearRoundRepository.findUsers(userType, assessmentRound);
        List<UserDataIdCodePair> pairs = industryDataAssignmentRepository.findDataIdCodePairsByAssessmentRoundId(assessmentRoundId);
        List<IndustrySurvey> surveys = industrySurveyRepository.findAllByYear(assessmentRound.getYear().getId());

        Map<Long, List<UserDataIdCodePair>> pairsByUserId = pairs.stream()
                .collect(Collectors.groupingBy(UserDataIdCodePair::userId));

        Map<Integer, String> surveyContentByNo = surveys.stream()
                .collect(Collectors.toMap(
                        IndustrySurvey::getSurveyNumber,
                        IndustrySurvey::getSurveyContent
                ));

        List<IndustryResponse> responses = industryResponseRepository.findAllByUserYearRound(assessmentRoundId);

        Map<Long, Map<Long, Map<Integer, IndustryResponse>>> responseIndex =
                responses.stream()
                        .filter(r -> r.getIndustryData() != null)
                        .filter(r -> r.getIndustrySurvey() != null)
                        .collect(Collectors.groupingBy(
                                r -> r.getUserYearRound().getUser().getId(),
                                Collectors.groupingBy(
                                        r -> r.getIndustryData().getId(),
                                        Collectors.toMap(
                                                r -> r.getIndustrySurvey().getSurveyNumber(),
                                                r -> r
                                        )
                                )
                        ));

        List<IndustryWeightedScore> weightedScores =
                industryWeightedScoreRepository.findAllByUserYearRound(assessmentRoundId);

        Map<Long, IndustryWeightedScore> weightedByUserId = weightedScores.stream()
                .collect(Collectors.toMap(
                        w -> w.getUserYearRound().getUser().getId(),
                        w -> w
                ));

        byte[] qualitativeXlsx = buildQualitativeAnswersXlsx(
                users,
                pairsByUserId,
                responseIndex,
                surveyCount,
                surveyContentByNo
        );

        byte[] weightedXlsx = buildWeightedScoresXlsx(
                users,
                weightedByUserId
        );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            zos.putNextEntry(new ZipEntry("industry_qualitative_answers.xlsx"));
            zos.write(qualitativeXlsx);
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("industry_weighted_scores.xlsx"));
            zos.write(weightedXlsx);
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to export evaluation excels zip", e);
        }
    }

    private byte[] buildQualitativeAnswersXlsx(
            List<UserEntity> users,
            Map<Long, List<UserDataIdCodePair>> pairsByUserId,
            Map<Long, Map<Long, Map<Integer, IndustryResponse>>> responseIndex,
            int surveyCount,
            Map<Integer, String> surveyContentByNo
    ) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("qualitative_answers");
            CellStyle headerStyle = createHeaderStyle(wb);

            Row header = sheet.createRow(0);
            int col = 0;

            header.createCell(col).setCellValue("memberId");
            header.getCell(col++).setCellStyle(headerStyle);

            header.createCell(col).setCellValue("memberName");
            header.getCell(col++).setCellStyle(headerStyle);

            header.createCell(col).setCellValue("dataId");
            header.getCell(col++).setCellStyle(headerStyle);

            header.createCell(col).setCellValue("dataCode");
            header.getCell(col++).setCellStyle(headerStyle);

            for (int qNo = 1; qNo <= surveyCount; qNo++) {
                String content = Optional.ofNullable(surveyContentByNo.get(qNo)).orElse("");
                String headerText = "Q" + qNo + (content.isBlank() ? "" : ": " + content);

                header.createCell(col).setCellValue(headerText);
                header.getCell(col++).setCellStyle(headerStyle);
            }

            int r = 1;

            for (UserEntity user : users) {
                List<UserDataIdCodePair> pairs = pairsByUserId.getOrDefault(user.getId(), List.of());

                for (UserDataIdCodePair pair : pairs) {
                    Row row = sheet.createRow(r++);
                    int c = 0;

                    row.createCell(c++).setCellValue(nvl(user.getId()));
                    row.createCell(c++).setCellValue(nvl(user.getName()));
                    row.createCell(c++).setCellValue(nvl(pair.dataId()));
                    row.createCell(c++).setCellValue(nvl(pair.dataCode()));

                    Map<Integer, IndustryResponse> bySurveyNo =
                            responseIndex.getOrDefault(user.getId(), Map.of())
                                    .getOrDefault(pair.dataId(), Map.of());

                    for (int qNo = 1; qNo <= surveyCount; qNo++) {
                        IndustryResponse resp = bySurveyNo.get(qNo);
                        row.createCell(c++).setCellValue(formatAnswer(resp));
                    }
                }
            }

            autosize(sheet, 4 + surveyCount);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build qualitative answers excel", e);
        }
    }

    private byte[] buildWeightedScoresXlsx(
            List<UserEntity> users,
            Map<Long, IndustryWeightedScore> weightedByUserId
    ) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("weighted_scores");
            CellStyle headerStyle = createHeaderStyle(wb);

            String[] headers = {
                    "memberId", "memberName",
                    "score1","score2","score3","score4","score5","score6","score7","score8"
            };

            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int r = 1;
            for (UserEntity user : users) {
                IndustryWeightedScore ws = weightedByUserId.get(user.getId());

                Row row = sheet.createRow(r++);
                int c = 0;

                row.createCell(c++).setCellValue(nvl(user.getId()));
                row.createCell(c++).setCellValue(nvl(user.getName()));

                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore1()));
                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore2()));
                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore3()));
                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore4()));
                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore5()));
                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore6()));
                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore7()));
                row.createCell(c++).setCellValue(ws == null ? "" : nvl(ws.getScore8()));
            }

            autosize(sheet, headers.length);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build weighted scores excel", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    private void autosize(Sheet sheet, int colCount) {
        for (int c = 0; c < colCount; c++) {
            sheet.autoSizeColumn(c);
        }
    }

    private String nvl(Object v) {
        return v == null ? "" : String.valueOf(v);
    }

    private String formatAnswer(IndustryResponse r) {
        if (r == null) return "";
        String num = (r.getNumberResponse() == null) ? "" : String.valueOf(r.getNumberResponse());
        String txt = (r.getTextResponse() == null || r.getTextResponse().isBlank()) ? "" : r.getTextResponse();

        if (!num.isBlank() && !txt.isBlank()) return num + "/" + txt;
        if (!num.isBlank()) return num;
        return txt;
    }

}
