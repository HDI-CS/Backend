package kr.co.hdi.survey.service;

import kr.co.hdi.crawl.repository.ProductImageRepository;
import kr.co.hdi.dataset.repository.BrandDatasetAssignmentRepository;
import kr.co.hdi.dataset.repository.ProductDatasetAssignmentRepository;
import kr.co.hdi.domain.assignment.entity.IndustryDataAssignment;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.assignment.repository.IndustryDataAssignmentRepository;
import kr.co.hdi.domain.assignment.repository.VisualDataAssignmentRepository;
import kr.co.hdi.domain.currentSurvey.entity.CurrentSurvey;
import kr.co.hdi.domain.currentSurvey.repository.CurrentSurveyRepository;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.repository.IndustryDataRepository;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.response.entity.VisualResponse;
import kr.co.hdi.domain.response.repository.IndustryResponseRepository;
import kr.co.hdi.domain.response.repository.VisualResponseRepository;
import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import kr.co.hdi.domain.survey.enums.SurveyType;
import kr.co.hdi.domain.survey.repository.IndustrySurveyRepository;
import kr.co.hdi.domain.survey.repository.VisualSurveyRepository;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.UserYearRoundRepository;
import kr.co.hdi.survey.domain.*;
import kr.co.hdi.survey.dto.response.*;
import kr.co.hdi.survey.dto.request.SurveyResponseRequest;
import kr.co.hdi.survey.dto.request.WeightedScoreRequest;
import kr.co.hdi.survey.exception.SurveyErrorCode;
import kr.co.hdi.survey.exception.SurveyException;
import kr.co.hdi.survey.repository.*;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.user.exception.AuthErrorCode;
import kr.co.hdi.domain.user.exception.AuthException;
import kr.co.hdi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyService {

    private final UserRepository userRepository;
    private final BrandSurveyRepository brandSurveyRepository;
    private final BrandResponseRepository brandResponseRepository;
    private final WeightedScoreRepository weightedScoreRepository;
    private final ProductResponseRepository productResponseRepository;
    private final ProductImageRepository productImageRepository;
    private final BrandDatasetAssignmentRepository brandDatasetAssignmentRepository;
    private final ProductDatasetAssignmentRepository productDatasetAssignmentRepository;
    private final ProductSurveyRepository productSurveyRepository;


    private final CurrentSurveyRepository currentSurveyRepository;
    private final UserYearRoundRepository userYearRoundRepository;

    private final VisualDataRepository visualDataRepository;
    private final VisualSurveyRepository visualSurveyRepository;
    private final VisualResponseRepository visualResponseRepository;
    private final VisualDataAssignmentRepository visualDataAssignmentRepository;

    private final IndustryDataRepository industryDataRepository;
    private final IndustrySurveyRepository industrySurveyRepository;
    private final IndustryResponseRepository industryResponseRepository;
    private final IndustryDataAssignmentRepository industryDataAssignmentRepository;

    /*
    [공통] 현재 평가 정보 조회
     */
    public CurrentSurvey getCurrentSurvey(DomainType type) {

        return currentSurveyRepository.findByDomainType(type)
                .orElseThrow();     // TODO : 에러 처리
    }

    /*
    평가할 시각 디자인 데이터 리스트 조회
     */
    @Transactional
    public List<ProductSurveyDataResponse> getAllVisualSurveys(Long userId) {

        // 현재 평가 정보
        CurrentSurvey currentSurvey = getCurrentSurvey(DomainType.VISUAL);
        Long assessmentRoundId = currentSurvey.getAssessmentRoundId();

        // 유저에게 할당된 데이터 리스트 조회
        List<VisualDataAssignment> assignments =
                visualDataAssignmentRepository.findAssignmentsByUserAndAssessmentRound(userId, assessmentRoundId);

        return assignments.stream()
                .map(ProductSurveyDataResponse::toResponseDto)
                .toList();
    }

    /*
    평가할 산업 디자인 데이터 리스트 조회
     */
    @Transactional
    public List<ProductSurveyDataResponse> getAllIndustrySurveys(Long userId) {

        // 현재 평가 정보
        CurrentSurvey currentSurvey = getCurrentSurvey(DomainType.INDUSTRY);
        Long assessmentRoundId = currentSurvey.getAssessmentRoundId();

        // 유저에게 할당된 데이터 리스트 조회
        List<IndustryDataAssignment> assignments =
                industryDataAssignmentRepository.findAssignmentsByUserAndAssessmentRound(userId, assessmentRoundId);

        return assignments.stream()
                .map(ProductSurveyDataResponse::toResponseDto)
                .toList();
    }

    /*
    시각 디자인 평가 데이터셋 + 응답 조회
     */
    public BrandSurveyDetailResponse getVisualSurveyDetail(Long dataId, Long userId) {

        // 데이터 조회
        VisualData visualData = visualDataRepository.findById(dataId)
                .orElseThrow();

        // 설문 문항 조회
        CurrentSurvey currentSurvey = getCurrentSurvey(DomainType.VISUAL);
        List<VisualSurvey> visualSurveys = visualSurveyRepository.findAllByYear(currentSurvey.getYearId());

        // 데이터에 대한 응답 조회
        List<VisualResponse> responses = visualResponseRepository.findAllByVisualDataIdAndUserId(dataId, userId);
        Map<Long, VisualResponse> responseMap = responses.stream()
                .collect(Collectors.toMap(r -> r.getVisualSurvey().getId(), r -> r));  // key: visualSurveyId, value: visualResponse

        // 설문 + 응답 dto
        List<NumberSurveyResponse> numberResponses = visualSurveys.stream()
                .filter(s -> s.getSurveyType() == SurveyType.NUMBER)
                .map(s -> NumberSurveyResponse.of(s, responseMap.get(s.getId())))
                .toList();
        List<TextSurveyResponse> textResponses = visualSurveys.stream()
                .filter(s -> s.getSurveyType() == SurveyType.TEXT)
                .map(s -> TextSurveyResponse.of(s, responseMap.get(s.getId())))
                .toList();

        return new BrandSurveyDetailResponse(
                BrandDatasetResponse.fromEntity(visualData),
                new SurveyResponse(
                        visualData.getBrandCode() + "_" + visualData.getSectorCategory(),
                        numberResponses,
                        textResponses
                ));
    }

    /*
    산업 디자인 평가 데이터셋 + 응답 조회
     */
    public ProductSurveyDetailResponse getIndustrySurveyDetail(Long dataId, Long userId) {

        // 데이터 조회
        IndustryData industryData = industryDataRepository.findById(dataId)
                .orElseThrow();

        // 설문 문항 조회
        CurrentSurvey currentSurvey = getCurrentSurvey(DomainType.INDUSTRY);
        List<IndustrySurvey> industrySurveys = industrySurveyRepository.findAllByYear(currentSurvey.getYearId());

        // 데이터에 대한 응답 조회
        List<IndustryResponse> responses = industryResponseRepository.findAllByIndustryDataIdAndUserId(dataId, userId);
        Map<Long, IndustryResponse> responseMap = responses.stream()
                .collect(Collectors.toMap(r -> r.getIndustrySurvey().getId(), r -> r));  // key: industrySurveyId, value: industryResponse

        // 설문 + 응답 dto
        List<NumberSurveyResponse> numberResponses = industrySurveys.stream()
                .filter(s -> s.getSurveyType() == SurveyType.NUMBER)
                .map(s -> NumberSurveyResponse.of(s, responseMap.get(s.getId())))
                .toList();
        List<TextSurveyResponse> textResponses = industrySurveys.stream()
                .filter(s -> s.getSurveyType() == SurveyType.TEXT)
                .map(s -> TextSurveyResponse.of(s, responseMap.get(s.getId())))
                .toList();

        return  new ProductSurveyDetailResponse(
                ProductDataSetResponse.fromEntity(industryData),
                new SurveyResponse(
                        industryData.getOriginalId() + "_" + industryData.getModelName(),
                        numberResponses,
                        textResponses
                ));
    }

    /*
    시각 디자인 응답 저장
     */
    @Transactional
    public void saveVisualSurveyResponse(Long dataId, Long userId, SurveyResponseRequest request) {

        CurrentSurvey currentSurvey = getCurrentSurvey(DomainType.VISUAL);
        UserYearRound userYearRound = userYearRoundRepository.findByAssessmentRoundIdAndUserId(currentSurvey.getAssessmentRoundId(), userId)
                .orElseThrow();

        //
        VisualDataAssignment assignment = visualDataAssignmentRepository.findByUserYearRoundIdAndVisualDataId(userYearRound.getId(), dataId)
                .orElseThrow();

        // 응답 조회 (없으면 생성)
        VisualResponse visualResponse = visualResponseRepository
                .findByUserYearRoundIdAndVisualSurveyIdAndVisualDataId(
                        userYearRound.getId(),
                        request.surveyId(),
                        dataId
                )
                .orElseGet(() -> {
                    assignment.incrementResponseCount();

                    return VisualResponse.builder()
                            .userYearRound(userYearRound)
                            .visualSurvey(visualSurveyRepository.getReferenceById(request.surveyId()))
                            .visualData(visualDataRepository.getReferenceById(dataId))
                            .build();
                });

        // 응답값 갱신
        VisualSurvey survey = visualResponse.getVisualSurvey();
        if (survey.getSurveyType() == SurveyType.NUMBER) {
            visualResponse.updateNumberResponse(request.response());
        } else if (survey.getSurveyType() == SurveyType.TEXT) {
            visualResponse.updateTextResponse(request.textResponse());
        }
        visualResponseRepository.save(visualResponse);
    }

    /*
    산업 디자인 응답 저장
     */
    @Transactional
    public void saveIndustrySurveyResponse(Long dataId, Long userId, SurveyResponseRequest request) {

        CurrentSurvey currentSurvey = getCurrentSurvey(DomainType.INDUSTRY);
        UserYearRound userYearRound = userYearRoundRepository.findByAssessmentRoundIdAndUserId(currentSurvey.getAssessmentRoundId(), userId)
                .orElseThrow();

        IndustryDataAssignment assignment = industryDataAssignmentRepository.findByUserYearRoundIdAndIndustryDataId(userYearRound.getId(), dataId)
                .orElseThrow();

        // 응답 조회 (없으면 생성)
        IndustryResponse industryResponse = industryResponseRepository
                .findByUserYearRoundIdAndIndustrySurveyIdAndIndustryDataId(
                        userYearRound.getId(),
                        request.surveyId(),
                        dataId
                )
                .orElseGet(() -> {
                    assignment.incrementResponseCount();

                    return IndustryResponse.builder()
                                .userYearRound(userYearRound)
                                .industrySurvey(industrySurveyRepository.getReferenceById(request.surveyId()))
                                .industryData(industryDataRepository.getReferenceById(dataId))
                                .build();
                });

        // 응답값 갱신
        IndustrySurvey survey = industryResponse.getIndustrySurvey();
        if (survey.getSurveyType() == SurveyType.NUMBER) {
            industryResponse.updateNumberResponse(request.response());
        } else if (survey.getSurveyType() == SurveyType.TEXT) {
            industryResponse.updateTextResponse(request.textResponse());
        }
        industryResponseRepository.save(industryResponse);
    }

    // 브랜드 응답 최종 제출
    @Transactional
    public void setBrandResponseStatusDone(Long brandResponseId, Long userId) {

        BrandResponse brandResponse = brandResponseRepository.findById(brandResponseId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.BRAND_RESPONSE_NOT_FOUND));

        if (!brandResponse.checkAllResponsesFilled())
            throw new SurveyException(SurveyErrorCode.INCOMPLETE_RESPONSE);

        brandResponse.updateResponseStatusToDone();
        brandResponseRepository.save(brandResponse);

        // 모든 설문에 응답했는지
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        long datasetCount = brandDatasetAssignmentRepository.countByUser(user);
        long responsedDatasetCount = brandResponseRepository.countByUserAndResponseStatus(user, ResponseStatus.DONE);
        if (datasetCount == responsedDatasetCount)
            user.updateSurveyDoneStatus();
        userRepository.save(user);
    }

    // 제품 응답 최종 제출
    @Transactional
    public void setProductResponseStatusDone(Long productResponseId, Long userId) {

        ProductResponse productResponse = productResponseRepository.findById(productResponseId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.PRODUCT_RESPONSE_NOT_FOUND));

        if (!productResponse.checkAllResponsesFilled())
            throw new SurveyException(SurveyErrorCode.INCOMPLETE_RESPONSE);

        productResponse.updateResponseStatusToDone();
        productResponseRepository.save(productResponse);

        // 모든 설문에 응답했는지
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        long datasetCount = productDatasetAssignmentRepository.countByUser(user);
        long responsedDatasetCount = productResponseRepository.countByUserAndResponseStatus(user, ResponseStatus.DONE);
        if (datasetCount == responsedDatasetCount)
            user.updateSurveyDoneStatus();
        userRepository.save(user);
    }

    // 가중치 평가
    @Transactional
    public void saveWeightedScores(Long userId, List<WeightedScoreRequest> requests) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        List<WeightedScore> scores = new ArrayList<>();
        for (WeightedScoreRequest request : requests) {

            WeightedScore score;

            if (request.id() != null) {
                // 기존 엔티티 조회
                score = weightedScoreRepository.findById(request.id())
                        .orElseThrow(() -> new IllegalArgumentException("WeightedScore not found with id: " + request.id()));

                // 값 갱신
                score.updateScores(
                        request.score1(),
                        request.score2(),
                        request.score3(),
                        request.score4(),
                        request.score5(),
                        request.score6(),
                        request.score7(),
                        request.score8()
                );

            } else {
                // 새 엔티티 생성
                score = WeightedScore.createWeightedScore(
                        user,
                        request.category(),
                        request.score1(),
                        request.score2(),
                        request.score3(),
                        request.score4(),
                        request.score5(),
                        request.score6(),
                        request.score7(),
                        request.score8()
                );
            }
            scores.add(score);
        }
        weightedScoreRepository.saveAll(scores);
    }

    public List<WeightedScoreResponse> getWeightedResponse(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        List<WeightedScore> scores = weightedScoreRepository.findByUser(user);
        return scores.stream()
                .map(score -> new WeightedScoreResponse(
                        score.getId(),
                        score.getCategory(),
                        score.getScore1(),
                        score.getScore2(),
                        score.getScore3(),
                        score.getScore4(),
                        score.getScore5(),
                        score.getScore6(),
                        score.getScore7(),
                        score.getScore8()
                ))
                .toList();
    }

    @Transactional
    public void checkSurveyDone(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (user.getUserType() == UserType.BRAND) {
            long datasetCount = brandDatasetAssignmentRepository.countByUser(user);
            long responsedDatasetCount = brandResponseRepository.countByUserAndResponseStatus(user, ResponseStatus.DONE);
            if (datasetCount == responsedDatasetCount)
                user.updateSurveyDoneStatus();
            else
                user.updateSurveyStatusToFalse();
            userRepository.save(user);
        }
        if (user.getUserType() == UserType.PRODUCT) {
            long datasetCount = productDatasetAssignmentRepository.countByUser(user);
            long responsedDatasetCount = productResponseRepository.countByUserAndResponseStatus(user, ResponseStatus.DONE);
            if (datasetCount == responsedDatasetCount)
                user.updateSurveyDoneStatus();
            else
                user.updateSurveyStatusToFalse();
            userRepository.save(user);
        }
    }
}
