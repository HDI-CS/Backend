package kr.co.hdi.admin.survey.service;

import kr.co.hdi.admin.survey.dto.request.SurveyDateRequest;
import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
import kr.co.hdi.admin.survey.dto.response.*;
import kr.co.hdi.admin.survey.exception.SurveyErrorCode;
import kr.co.hdi.admin.survey.exception.SurveyException;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import kr.co.hdi.domain.survey.enums.SurveyType;
import kr.co.hdi.domain.survey.repository.VisualSurveyRepository;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisualSurveyService implements SurveyService {

    private final YearRepository yearRepository;
    private final AssessmentRoundRepository assessmentRoundRepository;
    private final VisualSurveyRepository visualSurveyRepository;

    @Override
    public DomainType getDomainType() {
        return DomainType.VISUAL;
    }

    /*
    전체 평가 조회
     */
    @Override
    public List<SurveyResponse> getSurveys(DomainType type){
        List<Year> years = yearRepository.findAllByTypeAndDeletedAtIsNull(type);

        Map<Long, List<SurveyRoundResponse>> roundsByYearId =
                assessmentRoundRepository.findAllWithYearByDomainType(type)
                        .stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getYear().getId(),
                                LinkedHashMap::new,
                                Collectors.mapping(SurveyRoundResponse::from, Collectors.toList())
                        ));

        return years.stream()
                .map(y -> new SurveyResponse(
                        y.getId(),
                        y.getYear(),
                        y.getUpdatedAt(),
                        y.getCreatedAt(),
                        roundsByYearId.getOrDefault(y.getId(), List.of())
                ))
                .toList();
    }

    /*
    년도 평가 생성
     */
    @Override
    @Transactional
    public SurveyYearIdResponse createSurvey(DomainType type) {
        Year year = Year.create();
        yearRepository.save(year);
        return new SurveyYearIdResponse(year.getId());
    }

    /*
    년도 평가 이름 수정
     */
    @Override
    @Transactional
    public void updateYearFolderName(
            DomainType type,
            Long yearId,
            String newFolderName) {

        Year year = yearRepository.findById(yearId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.YEAR_NOT_FOUND));

        year.updateYear(newFolderName);
        yearRepository.save(year);
    }

    /*
    차수 평가 이름 수정
     */
    @Override
    @Transactional
    public void updateRoundFolderName(
            DomainType type,
            Long assessmentRoundId,
            String newFolderName) {

        AssessmentRound assessmentRound = assessmentRoundRepository.findById(assessmentRoundId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.ASSESSMENT_ROUND_NOT_FOUND));

        assessmentRound.updateRound(newFolderName);
        assessmentRoundRepository.save(assessmentRound);
    }


    /*
    차수 평가 생성
     */
    @Override
    @Transactional
    public SurveyRoundIdResponse createRound(DomainType type, Long yearId) {

        Year year = yearRepository.findById(yearId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.YEAR_NOT_FOUND));

        AssessmentRound assessmentRound = AssessmentRound.create(year);
        assessmentRoundRepository.save(assessmentRound );
        return new SurveyRoundIdResponse(assessmentRound.getId());
    }

    /*
    년도 평가 단일 설문 문항 수정
     */
    @Override
    @Transactional
    public void updateSurveyContent(
            DomainType type,
            Long questionId,
            String surveyContent) {

        VisualSurvey visualSurvey = visualSurveyRepository.findById(questionId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

        visualSurvey.updateSurvey(surveyContent);
        visualSurveyRepository.save(visualSurvey);
    }

    /*
    차수 평가 기간 생성 및 수정
     */
    @Override
    @Transactional
    public void upsertSurveyDate(
            DomainType type,
            Long assessmentRoundId,
            SurveyDateRequest request) {

        AssessmentRound assessmentRound = assessmentRoundRepository.findById(assessmentRoundId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.ASSESSMENT_ROUND_NOT_FOUND));

        assessmentRound.upsertDate(request);
        assessmentRoundRepository.save(assessmentRound);
    }

    /*
    년도 평가 설문 문항 생성
     */
    @Override
    @Transactional
    public void createSurveyQuestion(
            DomainType type,
            Long yearId,
            List<SurveyQuestionRequest> request
    ){

        Year year = yearRepository.findById(yearId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.YEAR_NOT_FOUND));

        List<VisualSurvey> surveys = request.stream()
                .map(req -> VisualSurvey.create(req,year))
                .toList();

        year.updateSurveyCount(request.size());
        yearRepository.save(year);
        visualSurveyRepository.saveAll(surveys);
    }

    /*
    년도 평가 설문 문항 조회
     */
    @Override
    public SurveyQuestionsByYearResponse getSurveyQuestions(
            DomainType type,
            Long yearId
    ){
        Year year = yearRepository.findById(yearId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.YEAR_NOT_FOUND));
        List<VisualSurvey> surveys = visualSurveyRepository.findAllByYear(yearId);

        Map<SurveyType, List<SurveyQuestionResponse>> grouped =
                surveys.stream()
                        .collect(Collectors.groupingBy(
                                VisualSurvey::getSurveyType,
                                LinkedHashMap::new,
                                Collectors.mapping(SurveyQuestionResponse::from, Collectors.toList())
                        ));

        List<SurveyQuestionTypeResponse> surveyQuestions =
                grouped.entrySet().stream()
                        .map(e -> new SurveyQuestionTypeResponse(e.getKey(), e.getValue()))
                        .toList();

        return new SurveyQuestionsByYearResponse(year.getYear(), surveyQuestions);
    }
}
