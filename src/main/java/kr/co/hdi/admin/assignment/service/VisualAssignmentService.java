package kr.co.hdi.admin.assignment.service;

import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;
import kr.co.hdi.admin.assignment.dto.request.AssignmentDataRequest;
import kr.co.hdi.admin.assignment.dto.response.AssessmentRoundResponse;
import kr.co.hdi.admin.assignment.dto.response.AssignmentDataResponse;
import kr.co.hdi.admin.assignment.dto.response.AssignmentResponse;
import kr.co.hdi.admin.assignment.exception.AssignmentErrorCode;
import kr.co.hdi.admin.assignment.exception.AssignmentException;
import kr.co.hdi.admin.data.dto.request.VisualDataIdsRequest;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.assignment.repository.VisualDataAssignmentRepository;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.user.entity.UserEntity;
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

    /*
    매칭 연도 목록 조회
     */
    public List<YearResponse> getAssignmentYearList() {

        List<Year> years = yearRepository.findAll();
        return years.stream()
                .map(YearResponse::from)
                .toList();
    }

    /*
    해당 연도의 매칭 차수 목록 조회
     */
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
    public List<AssignmentResponse> getDatasetAssignment(Long assessmentRoundId) {

        List<AssignmentRow> rows = visualDataAssignmentRepository.findVisualDataAssignment(assessmentRoundId);
        if (rows.isEmpty()) {
            return List.of();
        }

        Map<Long, List<AssignmentDataResponse>> dataMap =
                rows.stream()
                        .collect(Collectors.groupingBy(
                                AssignmentRow::userId,
                                HashMap::new,
                                Collectors.mapping(
                                        r -> new AssignmentDataResponse(
                                                r.dataId(),
                                                r.dataCode()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        Map<Long, String> nameMap =
                rows.stream()
                        .collect(Collectors.toMap(
                                AssignmentRow::userId,
                                AssignmentRow::username,
                                (a, b) -> a,
                                HashMap::new
                        ));

        return dataMap.entrySet().stream()
                .map(entry -> new AssignmentResponse(
                        entry.getKey(),
                        nameMap.get(entry.getKey()),
                        entry.getValue()
                ))
                .toList();
    }

    /*
    데이터셋 매칭 전문가별 조회
     */
    public AssignmentResponse getDatasetAssignmentByUser(Long assessmentRoundId, Long userId) {

        List<AssignmentRow> rows = visualDataAssignmentRepository.findVisualDataAssignmentByUser(assessmentRoundId, userId);
        if (rows.isEmpty()) {
            return null;
        }

        AssignmentRow userData = rows.get(0);
        return new AssignmentResponse(
                userData.userId(),
                userData.username(),
                rows.stream()
                        .map(r -> new AssignmentDataResponse(
                                r.dataId(),
                                r.dataCode()
                        ))
                        .toList()
        );
    }

    /*
    데이터셋 매칭 수정
     */
    @Transactional
    public void updateDatasetAssignment(
            Long assessmentRoundId,
            Long memberId,
            VisualDataIdsRequest request) {

        // 1. userYearRound 조회
        UserYearRound userYearRound = userYearRoundRepository.findByAssessmentRoundIdAndUserId(assessmentRoundId, memberId)
                .orElseThrow(() -> new AssignmentException(USER_NOT_PARTICIPATED_IN_ASSESSMENT_ROUND));

        // 2. 기존에 할당되어있는 데이터 조회
        List<VisualDataAssignment> existingAssignments =
                visualDataAssignmentRepository.findByUserYearRound(userYearRound);

        Set<Long> existingDataIds = existingAssignments.stream()
                .map(a -> a.getVisualData().getId())
                .collect(Collectors.toSet());
        Set<Long> requestedDataIds = new HashSet<>(request.ids());

        // 3. 삭제 대상
        for (VisualDataAssignment assignment : existingAssignments) {
            if (!requestedDataIds.contains(assignment.getVisualData().getId())) {
                visualDataAssignmentRepository.delete(assignment);
            }
        }

        // 4. 추가 대상
        Set<Long> toAddIds = requestedDataIds.stream()
                .filter(id -> !existingDataIds.contains(id))
                .collect(Collectors.toSet());

        if (!toAddIds.isEmpty()) {
            List<VisualData> visualDataList = visualDataRepository.findAllById(toAddIds);
            for(VisualData visualData : visualDataList) {
                visualDataAssignmentRepository.save(
                        VisualDataAssignment.builder()
                                .userYearRound(userYearRound)
                                .visualData(visualData)
                                .build()
                );
            }
        }
    }

    /*
    전문가와 데이터셋 매칭 등록
     */
    @Transactional
    public void createDatasetAssignment(
            Long assessmentRoundId,
            AssignmentDataRequest request) {

        UserEntity user = userRepository.findById(request.memberId())
                        .orElseThrow(() -> new AssignmentException(AssignmentErrorCode.USER_NOT_FOUND));
        AssessmentRound assessmentRound = assessmentRoundRepository.findById(assessmentRoundId)
                        .orElseThrow(() -> new AssignmentException(AssignmentErrorCode.ASSESSMENT_ROUND_NOT_FOUND));
        List<VisualData> visualDataList =
                visualDataRepository.findAllById(request.datasetsIds());

        // 1. YearRound에 유저 먼저 할당
        UserYearRound userYearRound = UserYearRound.builder()
                .user(user)
                .assessmentRound(assessmentRound)
                .build();
        userYearRoundRepository.save(userYearRound);

        // 2. 유저한테 전문가 할당
        List<VisualDataAssignment> assignments =
                visualDataList.stream()
                        .map(visualData ->
                                VisualDataAssignment.builder()
                                        .userYearRound(userYearRound)
                                        .visualData(visualData)
                                        .build()
                        )
                        .toList();
        visualDataAssignmentRepository.saveAll(assignments);
    }
}
