package kr.co.hdi.domain.assignment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.co.hdi.domain.assignment.entity.QVisualDataAssignment.visualDataAssignment;
import static kr.co.hdi.domain.data.entity.QVisualData.visualData;
import static kr.co.hdi.domain.user.entity.QUserEntity.userEntity;
import static kr.co.hdi.domain.year.entity.QAssessmentRound.assessmentRound1;
import static kr.co.hdi.domain.year.entity.QUserYearRound.userYearRound;

@RequiredArgsConstructor
public class VisualDataAssignmentRepositoryImpl implements  VisualDataAssignmentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AssignmentRow> findVisualDataAssignment(Long assessmentRoundId) {

        return queryFactory
                .select(Projections.constructor(
                        AssignmentRow.class,
                        userEntity.id,
                        userEntity.name,
                        visualData.id,
                        visualData.brandCode
                ))
                .from(assessmentRound1)
                .join(userYearRound).on(
                        userYearRound.assessmentRound.eq(assessmentRound1),
                        userYearRound.deletedAt.isNull()
                )
                .join(userEntity).on(
                        userYearRound.user.eq(userEntity),
                        userEntity.deletedAt.isNull()
                )
                .join(visualDataAssignment).on(
                        visualDataAssignment.userYearRound.eq(userYearRound),
                        visualDataAssignment.deletedAt.isNull()
                )
                .join(visualData).on(
                        visualDataAssignment.visualData.eq(visualData),
                        visualData.deletedAt.isNull()
                )
                .where(
                        assessmentRound1.id.eq(assessmentRoundId),
                        assessmentRound1.domainType.eq(DomainType.VISUAL),
                        assessmentRound1.deletedAt.isNull()
                )
                .orderBy(
                        userEntity.id.asc(),
                        visualData.id.asc()
                )
                .fetch();
    }

    @Override
    public List<AssignmentRow> findVisualDataAssignmentByUser(Long assessmentRoundId, Long userId) {

        return queryFactory
                .select(Projections.constructor(
                        AssignmentRow.class,
                        userEntity.id,
                        userEntity.name,
                        visualData.id,
                        visualData.brandCode
                ))
                .from(assessmentRound1)
                .join(userYearRound).on(
                        userYearRound.assessmentRound.eq(assessmentRound1),
                        userYearRound.deletedAt.isNull()
                )
                .join(userEntity).on(
                        userYearRound.user.eq(userEntity),
                        userEntity.deletedAt.isNull()
                )
                .join(visualDataAssignment).on(
                        visualDataAssignment.userYearRound.eq(userYearRound),
                        visualDataAssignment.deletedAt.isNull()
                )
                .join(visualData).on(
                        visualDataAssignment.visualData.eq(visualData),
                        visualData.deletedAt.isNull()
                )
                .where(
                        userEntity.id.eq(userId),
                        assessmentRound1.id.eq(assessmentRoundId),
                        assessmentRound1.domainType.eq(DomainType.VISUAL),
                        assessmentRound1.deletedAt.isNull()
                )
                .orderBy(
                        userEntity.id.asc(),
                        visualData.id.asc()
                )
                .fetch();
    }
}
