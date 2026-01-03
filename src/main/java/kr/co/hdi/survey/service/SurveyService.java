package kr.co.hdi.survey.service;

import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;
import kr.co.hdi.crawl.repository.ProductImageRepository;
import kr.co.hdi.dataset.domain.BrandDatasetAssignment;
import kr.co.hdi.dataset.domain.ProductDatasetAssignment;
import kr.co.hdi.dataset.repository.BrandDatasetAssignmentRepository;
import kr.co.hdi.dataset.repository.ProductDatasetAssignmentRepository;
import kr.co.hdi.domain.assignment.repository.VisualDataAssignmentRepository;
import kr.co.hdi.domain.currentSurvey.entity.CurrentSurvey;
import kr.co.hdi.domain.currentSurvey.repository.CurrentSurveyRepository;
import kr.co.hdi.domain.data.entity.VisualData;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
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
    private final VisualDataAssignmentRepository visualDataAssignmentRepository;

    /*
    [공통] 현재 평가 정보 조회
     */
    public CurrentSurvey getCurrentSurvey(DomainType type) {

        return currentSurveyRepository.findByDomainType(type)
                .orElseThrow();     // TODO : 에러 처리
    }

    // 평가할 브랜드 리스트 조회
//    @Transactional
//    public List<ProductSurveyDataResponse> getAllBrandSurveys(Long userId) {
//
//        // 현재 평가 정보
//        CurrentSurvey currentSurvey = getCurrentSurvey(DomainType.VISUAL);
//        Long yearId = currentSurvey.getYearId();
//        Long assessmentRoundId = currentSurvey.getAssessmentRoundId();
//
//        // 유저에게 할당된 데이터 리스트 조회
//        List<VisualData> visualData = visualDataAssignmentRepository.findVisualDataByUserAndAssessmentRound(userId, assessmentRoundId);
//
//    }

    // 평가할 제품 리스트 조회
    @Transactional
    public List<ProductSurveyDataResponse> getAllProductSurveys(Long userId) {

        // 유저의 전체 product assignment 조회
        List<ProductDatasetAssignment> assignments = productDatasetAssignmentRepository.findAllByUserId(userId);

        if (assignments.isEmpty()) {
            return List.of(); // 배정 자체가 없으면 빈 리스트 반환
        }

        // 유저의 기존 product 응답 조회
        List<ProductResponse> existingResponses = productResponseRepository.findAllByUserId(userId);

        // Product ID 기준으로 Map 구성
        Map<Long, ProductResponse> responseMap = existingResponses.stream()
                .collect(Collectors.toMap(
                        pr -> pr.getProduct().getId(),
                        pr -> pr
                ));

        // 응답이 없는 product에 대해 새로 생성
        List<ProductResponse> missingResponses = assignments.stream()
                .map(ProductDatasetAssignment::getProduct)
                .filter(product -> !responseMap.containsKey(product.getId()))
                .map(product -> ProductResponse.createProductResponse(assignments.get(0).getUser(), product))
                .toList();

        // 새 응답 저장 및 합치기
        if (!missingResponses.isEmpty()) {
            List<ProductResponse> saved = productResponseRepository.saveAll(missingResponses);
            saved.forEach(pr -> responseMap.put(pr.getProduct().getId(), pr));
        }

        // 정렬 및 DTO 변환
        return responseMap.values().stream()
                .sorted(Comparator.comparing(ProductResponse::getCreatedAt).reversed())
                .map(pr -> new ProductSurveyDataResponse(
                        pr.getProduct().getProductName(),
                        productImageRepository.findByProductId(pr.getProduct().getId()).getFrontPath(),
                        pr.getResponseStatus(),
                        pr.getId()
                ))
                .toList();

//        List<ProductResponse> productResponses = productResponseRepository.findAllByUserId(userId);
//        if (productResponses.isEmpty()) {
//            List<ProductDatasetAssignment> assignments = productDatasetAssignmentRepository.findAllByUserId(userId);
//            List<ProductResponse> newResponses = assignments.stream()
//                    .map(a -> ProductResponse.createProductResponse(a.getUser(), a.getProduct()))
//                    .toList();
//            productResponses = productResponseRepository.saveAll(newResponses);
//        }
//
//        return productResponses.stream()
//                .sorted(Comparator.comparing(productResponse -> productResponse.getProduct().getId()))
//                .map(productResponse -> new ProductSurveyDataResponse(
//                        productResponse.getProduct().getProductName(),
//                        productImageRepository.findByProductId(productResponse.getProduct().getId()).getFrontPath(),
//                        productResponse.getResponseStatus(),
//                        productResponse.getId()
//                ))
//                .toList();
    }

    public ProductSurveyDetailResponse getProductSurveyDetail(Long productResponseId) {
        ProductResponse productResponse = productResponseRepository.findById(productResponseId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.PRODUCT_RESPONSE_NOT_FOUND));

        ProductSurvey productSurvey = productSurveyRepository.findById(1L)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

        ProductDataSetResponse dataSetResponse = ProductDataSetResponse.from(productResponse.getProduct(),
                productImageRepository.findByProductId(productResponse.getProduct().getId()));

        ProductSurveyResponse productSurveyResponse = ProductSurveyResponse.from(productSurvey, productResponse);

        return new ProductSurveyDetailResponse(dataSetResponse, productSurveyResponse);
    }


    @Transactional
    public void saveProductSurveyResponse(Long productResponseId, SurveyResponseRequest request, Long userId) {

        ProductResponse productResponse = productResponseRepository.findById(productResponseId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.PRODUCT_RESPONSE_NOT_FOUND));

        checkUserAuthorization(productResponse, userId);

        // 정량 평가
        if (request.index() != null) {
            productResponse.updateResponse(request.index(), request.response());
        } else {  // 정성 평가
            productResponse.updateTextResponse(request.textResponse());
        }

        productResponse.updateResponseStatus();
        productResponseRepository.save(productResponse);
    }

    private void checkUserAuthorization(ProductResponse productResponse, Long userId) {
        if (!productResponse.getUser().getId().equals(userId)) {
            throw new SurveyException(SurveyErrorCode.UNAUTHORIZED_ACCESS);
        }
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



    // 브랜드 평가 데이터셋 + 응답 조회
    public BrandSurveyDetailResponse getBrandSurveyDetail(Long brandResponseId) {

        BrandResponse brandResponse = brandResponseRepository.findById(brandResponseId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.BRAND_RESPONSE_NOT_FOUND));

        BrandSurvey brandSurvey = brandSurveyRepository.findById(1L)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.SURVEY_NOT_FOUND));

        BrandDatasetResponse brandDatasetResponse = BrandDatasetResponse.fromEntity(brandResponse.getBrand());
        String dataId = brandResponse.getBrand().getBrandCode() + "_" + brandResponse.getBrand().getSectorCategory();
        BrandSurveyResponse brandSurveyResponse = BrandSurveyResponse.fromEntity(dataId, brandSurvey,brandResponse);
        return new BrandSurveyDetailResponse(brandDatasetResponse, brandSurveyResponse);
    }

    // 브랜드 응답 저장
    @Transactional
    public void saveBrandSurveyResponse(Long brandResponseId, SurveyResponseRequest request) {

        BrandResponse brandResponse = brandResponseRepository.findById(brandResponseId)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.BRAND_RESPONSE_NOT_FOUND));

        // 정량 평가
        if (request.index() != null) {
            brandResponse.updateResponse(request.index(), request.response());
        } else {   // 정성 평가
            brandResponse.updateTextResponse(request.textResponse());
        }

        brandResponse.updateResponseStatus();
        brandResponseRepository.save(brandResponse);
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
//            scores.add(
//                    WeightedScore.createWeightedScore(
//                            user,
//                            request.category(),
//                            request.score1(),
//                            request.score2(),
//                            request.score3(),
//                            request.score4(),
//                            request.score5(),
//                            request.score6(),
//                            request.score7(),
//                            request.score8())
//            );
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
