package kr.co.hdi.admin.evaluation.service;

import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.response.entity.VisualResponse;
import kr.co.hdi.domain.response.repository.VisualResponseRepository;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import kr.co.hdi.domain.survey.repository.VisualSurveyRepository;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.repository.UserRepository;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.domain.year.repository.UserYearRoundRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class VisualEvaluationUploadService {

    private final VisualDataRepository visualDataRepository;
    private final UserRepository userRepository;
    private final VisualSurveyRepository visualSurveyRepository;
    private final UserYearRoundRepository userYearRoundRepository;
    private final VisualResponseRepository visualResponseRepository;

    @Transactional
    public void importVisualEvaluations(String filePath, Long roundId) throws IOException {
        File file = new File(filePath);

        // 1. 경로 검증 (Is a directory 에러 방지)
        if (!file.exists()) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + filePath);
        }
        if (file.isDirectory()) {
            throw new RuntimeException("입력하신 경로는 폴더입니다. 파일명까지 포함해주세요: " + filePath);
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreHeaderCase(true)
                .build();

        // 2. 파일 읽기 (인코딩 문제 방지를 위해 EUC-KR 또는 UTF-8 시도)
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), Charset.forName("UTF-8"));
             CSVParser csvParser = format.parse(reader)) {

            // 3. BOM 및 컬럼명 불일치 해결을 위한 헤더 매핑
            String actualDataCodeKey = "";
            String actualUserNameKey = "";
            String actualQualitativeKey = "";

            for (String header : csvParser.getHeaderNames()) {
                if (header.contains("data_code")) actualDataCodeKey = header;
                if (header.contains("user_name")) actualUserNameKey = header;
                if (header.contains("정성평가")) actualQualitativeKey = header;
            }

            for (CSVRecord record : csvParser) {
                String data_code = record.get(actualDataCodeKey).trim();
                String user_name = record.get(actualUserNameKey).trim();
                String text_response = record.get(actualQualitativeKey);

                // DB 조회
                VisualData visualData = visualDataRepository.findByBrandCode(data_code)
                        .orElseThrow(() -> new RuntimeException("제품 없음: " + data_code));

                UserEntity user = userRepository.findByName(user_name)
                        .orElseThrow(() -> new RuntimeException("전문가 없음: " + user_name));

                UserYearRound userYearRound = userYearRoundRepository.findByAssessmentRoundIdAndUserId(roundId, user.getId())
                        .orElseThrow(() -> new RuntimeException("차수 정보 없음"));

                // 4. 문항별 점수 저장 루프
                for (String header : csvParser.getHeaderNames()) {
                    if (header.matches("^\\d+\\..*")) {
                        String surveyCode = header.replaceFirst("^\\d+\\.", "").trim(); // "1. VI_USB_AP" -> "VI_USB_AP"
                        surveyCode = surveyCode.split("\\s+")[0].trim();

                        String finalSurveyCode = surveyCode;
                        VisualSurvey vs = visualSurveyRepository.findBySurveyCode(surveyCode)
                                .orElseThrow(() -> new RuntimeException("문항 없음: " + finalSurveyCode));

                        String val = record.get(header);
                        int score = 0;
                        if (val != null) {
                            val = val.trim();
                            if (!val.isEmpty()) score = (int) Math.round(Double.parseDouble(val));
                        }

                        visualResponseRepository.save(VisualResponse.builder()
                                .visualData(visualData)
                                .visualSurvey(vs)
                                .numberResponse(score)
                                .userYearRound(userYearRound)
                                .build());
                    }
                }

                // 5. 정성평가(VI_TEXT) 저장
                VisualSurvey textSurvey = visualSurveyRepository.findBySurveyCode("VI_TEXT")
                        .orElseThrow(() -> new RuntimeException("VI_TEXT 문항 없음"));

                visualResponseRepository.save(VisualResponse.builder()
                        .visualData(visualData)
                        .visualSurvey(textSurvey)
                        .textResponse(text_response)
                        .userYearRound(userYearRound)
                        .build());
            }
        }
    }
}
