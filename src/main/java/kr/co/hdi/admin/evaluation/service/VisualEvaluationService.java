package kr.co.hdi.admin.evaluation.service;

import kr.co.hdi.admin.evaluation.dto.enums.EvaluationType;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationAnswerByDataResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationAnswerByMemberResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusByMemberResponse;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusResponse;
import kr.co.hdi.admin.evaluation.exeption.EvaluationErrorCode;
import kr.co.hdi.admin.evaluation.exeption.EvaluationException;
import kr.co.hdi.domain.assignment.query.DataIdCodePair;
import kr.co.hdi.domain.assignment.query.UserDataPair;
import kr.co.hdi.domain.assignment.repository.VisualDataAssignmentRepository;
import kr.co.hdi.domain.response.entity.VisualResponse;
import kr.co.hdi.domain.response.entity.VisualWeightedScore;
import kr.co.hdi.domain.response.repository.VisualResponseRepository;
import kr.co.hdi.domain.response.repository.VisualWeightedScoreRepository;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import kr.co.hdi.domain.survey.repository.VisualSurveyRepository;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.user.repository.UserRepository;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisualEvaluationService implements EvaluationService {

    private final UserRepository userRepository;
    private final VisualResponseRepository visualResponseRepository;
    private final VisualWeightedScoreRepository visualWeightedScoreRepository;
    private final VisualDataAssignmentRepository visualDataAssignmentRepository;
    private final AssessmentRoundRepository assessmentRoundRepository;
    private final VisualSurveyRepository visualSurveyRepository;

    @Override
    public DomainType getDomainType() {
        return DomainType.VISUAL;
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
                        ? userRepository.findByUserTypeAndDeletedAtIsNull(userType)
                        : userRepository.findByUserTypeAndSearch(userType, q);

        List<UserDataPair> dataAssignments = visualDataAssignmentRepository.findUserDataPairsByAssessmentRoundId(assessmentRoundId);
        List<VisualWeightedScore> weightedScores = visualWeightedScoreRepository.findAllByUserYearRound(assessmentRoundId);
        List<VisualResponse> qualitativeResponses = visualResponseRepository.findAllByUserYearRound(assessmentRoundId);

        // 전문가-할당데이터 그룹핑 (모든 데이터)
        Map<Long, List<Long>> dataIdsByUserId =
                dataAssignments.stream().collect(groupingBy(
                        UserDataPair::userId,
                        mapping(UserDataPair::dataId, toList())
                ));

        // 전문가-할당데이터-정량평가응답 그룹핑 (응답한 데이터)
        Map<Long, Map<Long, List<VisualResponse>>> responsesByUser =
                qualitativeResponses.stream()
                        .collect(groupingBy(
                                r -> r.getUserYearRound().getUser().getId(),
                                groupingBy(r -> r.getVisualData().getId())
                        ));

        // 전문가-가중치평가응답
        Map<Long, VisualWeightedScore> weightedByUserId =
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
            Map<Long, List<VisualResponse>> userResponses,
            VisualWeightedScore weightedScore,
            List<Long> userDataIds,
            Integer surveyCount
    ) {
        // 데이터 id 오름차순으로 완료/미완료 상태를 담는 list
        List<EvaluationStatusResponse> statuses = userDataIds.stream()
                .sorted()
                .map(dataId -> {
                    List<VisualResponse> list = userResponses.get(dataId);
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
    private boolean isQualitativeDone(List<VisualResponse> list, Integer surveyCount) {
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
    private boolean isWeightedDone(VisualWeightedScore ws) {
        if (ws == null) return false;

        return Stream.of(
                ws.getScore1(), ws.getScore2(), ws.getScore3(), ws.getScore4(),
                ws.getScore5(), ws.getScore6(), ws.getScore7(), ws.getScore8()
        ).noneMatch(Objects::isNull);
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

        List<DataIdCodePair> pairs = visualDataAssignmentRepository.findDataIdCodePairsByAssessmentRoundIdAndUserId(assessmentRoundId, memberId);
        List<VisualSurvey> surveys = visualSurveyRepository.findAllByYear(assessmentRound.getYear().getId());
        List<VisualResponse> responses = visualResponseRepository.findAllByAssessmentRoundIdAndMemberId(assessmentRoundId, memberId);

        Map<Long, List<VisualResponse>> responsesByDataId = responses.stream()
                .filter(r -> r.getVisualData() != null)
                .collect(Collectors.groupingBy(r -> r.getVisualData().getId()));

        List<EvaluationAnswerByDataResponse> surveyDatas = pairs.stream()
                .map(pair -> EvaluationAnswerByDataResponse.ofVisual(
                        pair,
                        surveys,
                        responsesByDataId.getOrDefault(pair.dataId(), List.of())
                ))
                .toList();

        return EvaluationAnswerByMemberResponse.of(user, surveyDatas);
    }

}
