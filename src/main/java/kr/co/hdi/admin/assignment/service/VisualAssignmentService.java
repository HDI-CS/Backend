package kr.co.hdi.admin.assignment.service;

import kr.co.hdi.admin.assignment.dto.query.AssignmentDiff;
import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;
import kr.co.hdi.admin.assignment.dto.request.AssignmentDataRequest;
import kr.co.hdi.admin.assignment.dto.response.AssessmentRoundResponse;
import kr.co.hdi.admin.assignment.dto.response.AssignmentDataResponse;
import kr.co.hdi.admin.assignment.dto.response.AssignmentResponse;
import kr.co.hdi.admin.assignment.exception.AssignmentErrorCode;
import kr.co.hdi.admin.assignment.exception.AssignmentException;
import kr.co.hdi.admin.data.dto.request.DataIdsRequest;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.assignment.repository.VisualDataAssignmentRepository;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.exception.AuthErrorCode;
import kr.co.hdi.domain.user.exception.AuthException;
import kr.co.hdi.domain.user.repository.UserRepository;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
import kr.co.hdi.domain.year.repository.UserYearRoundRepository;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static kr.co.hdi.admin.assignment.exception.AssignmentErrorCode.USER_NOT_PARTICIPATED_IN_ASSESSMENT_ROUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisualAssignmentService implements AssignmentService {

    private final YearRepository yearRepository;
    private final UserRepository userRepository;
    private final VisualDataRepository visualDataRepository;
    private final UserYearRoundRepository userYearRoundRepository;
    private final AssessmentRoundRepository assessmentRoundRepository;
    private final VisualDataAssignmentRepository visualDataAssignmentRepository;

    @Override
    public DomainType getDomainType() {
        return DomainType.VISUAL;
    }

    /*
    매칭 연도 목록 조회
     */
    @Override
    public List<YearResponse> getAssignmentYearList() {

        List<Year> years = yearRepository.findAll();
        return years.stream()
                .map(YearResponse::from)
                .toList();
    }

    /*
    해당 연도의 매칭 차수 목록 조회
     */
    @Override
    public List<AssessmentRoundResponse> getAssessmentRoundList(Long yearId) {

        List<AssessmentRound> assessmentRounds = assessmentRoundRepository.findByDomainTypeAndYear(DomainType.VISUAL, yearId);
        return assessmentRounds.stream()
                .map(ar -> new AssessmentRoundResponse(
                        ar.getId(),
                        ar.getAssessmentRound()
                ))
                .toList();
    }

    /*
    해당 차수의 데이터셋 매칭 전체 조회
     */
    @Override
    public List<AssignmentResponse> getDatasetAssignment(Long assessmentRoundId) {

        List<AssignmentRow> rows = visualDataAssignmentRepository.findVisualDataAssignment(assessmentRoundId);
        if (rows.isEmpty()) {
            return List.of();
        }

        return rows.stream()
                .collect(Collectors.groupingBy(AssignmentRow::userId))
                .values()
                .stream()
                .map(this::toAssignmentResponse)
                .toList();
    }

    /*
    데이터셋 매칭 전문가별 조회
     */
    @Override
    public AssignmentResponse getDatasetAssignmentByUser(Long assessmentRoundId, Long userId) {

        List<AssignmentRow> rows = visualDataAssignmentRepository.findVisualDataAssignmentByUser(assessmentRoundId, userId);
        if (rows.isEmpty()) {
            return null;
        }

        return toAssignmentResponse(rows);
    }

    private AssignmentResponse toAssignmentResponse(List<AssignmentRow> rows) {
        AssignmentRow first = rows.get(0);

        return new AssignmentResponse(
                first.userId(),
                first.username(),
                rows.stream()
                        .map(this::toAssignmentData)
                        .toList()
        );
    }

    private AssignmentDataResponse toAssignmentData(AssignmentRow row) {
        return new AssignmentDataResponse(
                row.dataId(),
                row.dataCode()
        );
    }

    /*
    데이터셋 매칭 수정
    1. 기존에 할당된 데이터 id 조회
    2. 새로 요청된 데이터 id 조회
    3. 갱신된 정보 파악 (삭제할 id, 추가할 id)
     */
    @Override
    @Transactional
    public void updateDatasetAssignment(
            Long assessmentRoundId,
            Long memberId,
            DataIdsRequest request) {

        UserYearRound userYearRound = getUserYearRound(assessmentRoundId, memberId);
        AssignmentDiff diff = calculateDiff(userYearRound, request);

        deleteRemovedAssignments(userYearRound, diff);
        addNewAssignments(userYearRound, diff);
    }

    private UserYearRound getUserYearRound(Long assessmentRoundId, Long memberId) {

        return userYearRoundRepository.findByAssessmentRoundIdAndUserId(assessmentRoundId, memberId)
                .orElseThrow(() -> new AssignmentException(USER_NOT_PARTICIPATED_IN_ASSESSMENT_ROUND));
    }

    private AssignmentDiff calculateDiff(UserYearRound userYearRound, DataIdsRequest request) {

        // 기존에 할당된 데이터 ids
        Set<Long> existingIds = visualDataAssignmentRepository.findByUserYearRound(userYearRound)
                .stream()
                .map(a -> a.getVisualData().getId())
                .collect(Collectors.toSet());

        // 새로 수정된 데이터 ids
        Set<Long> requestedIds = new HashSet<>(request.ids());

        return AssignmentDiff.of(existingIds, requestedIds);
    }

    private void deleteRemovedAssignments(UserYearRound userYearRound, AssignmentDiff diff) {

        if (diff.toRemove().isEmpty()) {
            return;
        }
        visualDataAssignmentRepository.deleteByUserYearRoundAndVisualDataIds(userYearRound, diff.toRemove());
    }

    private void addNewAssignments(UserYearRound userYearRound, AssignmentDiff diff) {

        if (diff.toAdd().isEmpty()) {
            return;
        }

        List<VisualData> visualDataList = visualDataRepository.findAllById(diff.toAdd());
        visualDataAssignmentRepository.saveAll(
                VisualDataAssignment.createAll(userYearRound, visualDataList)
        );
    }

    /*
    전문가와 데이터셋 매칭 등록
    1. 해당 연도의 차수에 전문가 등록 (UserYearRound 등록)
    2. 전문가에게 데이터셋 할당 (userYearRound와 dataIds를 Assignment에 저장)
     */
    @Override
    @Transactional
    public void createDatasetAssignment(
            Long assessmentRoundId,
            AssignmentDataRequest request) {

        UserEntity user = getUser(request.memberId());
        AssessmentRound assessmentRound = getAssessmentRound(assessmentRoundId);
        List<VisualData> visualDataList = getVisualData(request.datasetsIds());

        UserYearRound userYearRound = createUserYearRound(user, assessmentRound);

        createVisualDataAssignments(userYearRound, visualDataList);
    }

    private UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }

    private AssessmentRound getAssessmentRound(Long id) {
        return assessmentRoundRepository.findById(id)
                .orElseThrow(() -> new AssignmentException(AssignmentErrorCode.ASSESSMENT_ROUND_NOT_FOUND));
    }

    private List<VisualData> getVisualData(List<Long> ids) {
        return visualDataRepository.findAllById(ids);
    }

    private UserYearRound createUserYearRound(
            UserEntity user,
            AssessmentRound assessmentRound) {

        UserYearRound userYearRound = UserYearRound.builder()
                .user(user)
                .assessmentRound(assessmentRound)
                .build();

        return userYearRoundRepository.save(userYearRound);
    }

    private void createVisualDataAssignments(
            UserYearRound userYearRound,
            List<VisualData> visualDataList) {

        visualDataAssignmentRepository.saveAll(
                VisualDataAssignment.createAll(userYearRound, visualDataList)
        );
    }
}
