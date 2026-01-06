package kr.co.hdi.admin.user.service;

import kr.co.hdi.admin.user.dto.request.ExpertInfoRequest;
import kr.co.hdi.admin.user.dto.request.ExpertInfoUpdateRequest;
import kr.co.hdi.admin.user.dto.response.ExpertInfoResponse;
import kr.co.hdi.domain.user.entity.Role;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.user.exception.AuthErrorCode;
import kr.co.hdi.domain.user.exception.AuthException;
import kr.co.hdi.domain.user.repository.UserRepository;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.domain.year.repository.UserYearRoundRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpertUserService {

    private final UserRepository userRepository;
    private final UserYearRoundRepository userYearRoundRepository;

    /*
    특정 분야의 등록된 전문가 전체 조회
     */
    public List<ExpertInfoResponse> getExpertInfo(UserType type) {

        List<UserEntity> users = userRepository.findExpertByType(type, Role.USER);
        return attachRounds(type, users);
    }

    /*
    전문가 페이지 검색
     */
    public List<ExpertInfoResponse> searchExpert(UserType type, String q) {

        List<UserEntity> users = userRepository.searchExperts(type, q);
        return attachRounds(type, users);
    }

    private List<ExpertInfoResponse> attachRounds(UserType type, List<UserEntity> users) {

        List<UserYearRound> userYearRounds =
                userYearRoundRepository.findAllByUserType(type);

        Map<UserEntity, List<UserYearRound>> grouped =
                userYearRounds.stream()
                        .collect(Collectors.groupingBy(UserYearRound::getUser));

        return users.stream()
                .sorted(Comparator.comparing(UserEntity::getId))
                .map(user -> {
                    List<UserYearRound> rounds = grouped.getOrDefault(user, List.of());
                    return ExpertInfoResponse.from(user, toRoundStrings(rounds));
                })
                .toList();
    }

    private List<String> toRoundStrings(List<UserYearRound> rounds) {
        return rounds.stream()
                .map(this::toRoundString)
                .toList();
    }

    private String toRoundString(UserYearRound uyr) {
        var ar = uyr.getAssessmentRound();
        return ar.getYear().getYear() + "년 " + ar.getAssessmentRound() + "차수";
    }

    /*
    새로운 전문가 등록
     */
    @Transactional
    public void registerExpert(
            UserType type, ExpertInfoRequest request
    ) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_EXISTS);
        }

        UserEntity newUser = UserEntity.createExpert(request, type, request.password());
        userRepository.save(newUser);
    }

    /*
    전문가 정보 수정
     */
    @Transactional
    public void updateExpertInfo(ExpertInfoUpdateRequest request, Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        user.updateInfo(request);
        userRepository.save(user);
    }

    /*
    전문가 인적사항 엑셀 다운로드
     */
    public byte[] exportExpertInfo(UserType type) {

        List<UserEntity> rows = userRepository.findExpertByType(type, Role.USER);

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("expert_information");

            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {
                    "ID", "Name", "Email", "Password", "PhoneNumber", "Gender",
                    "Age", "Career", "Academic", "Expertise", "Company", "Note"
            };

            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < headers.length; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(headers[c]);
                cell.setCellStyle(headerStyle);
            }

            int r = 1;
            for (UserEntity i : rows) {
                Row row = sheet.createRow(r++);

                int c = 0;
                row.createCell(c++).setCellValue(nvl(i.getId()));
                row.createCell(c++).setCellValue(nvl(i.getName()));
                row.createCell(c++).setCellValue(nvl(i.getEmail()));
                row.createCell(c++).setCellValue(nvl(i.getPassword()));
                row.createCell(c++).setCellValue(nvl(i.getPhoneNumber()));
                row.createCell(c++).setCellValue(nvl(i.getGender()));
                row.createCell(c++).setCellValue(nvl(i.getAge()));
                row.createCell(c++).setCellValue(nvl(i.getCareer()));
                row.createCell(c++).setCellValue(nvl(i.getAcademic()));
                row.createCell(c++).setCellValue(nvl(i.getExpertise()));
                row.createCell(c++).setCellValue(nvl(i.getCompany()));
                row.createCell(c++).setCellValue(nvl(i.getNote()));
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
