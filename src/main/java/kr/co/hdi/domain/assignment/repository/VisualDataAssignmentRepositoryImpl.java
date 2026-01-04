package kr.co.hdi.domain.assignment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.user.entity.Role;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
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
    public LocalDateTime findLastModifiedAtByAssessmentRound(Long assessmentRoundId) {
        return queryFactory
                .select(visualDataAssignment.updatedAt.max())
                .from(visualDataAssignment)
                .join(visualDataAssignment.userYearRound, userYearRound)
                .join(userYearRound.assessmentRound, assessmentRound1)
                .where(
                        assessmentRound1.id.eq(assessmentRoundId),
                        assessmentRound1.deletedAt.isNull(),
                        visualDataAssignment.deletedAt.isNull()
                )
                .fetchOne();
    }

    @Override
    public List<AssignmentRow> findVisualDataAssignment(Long assessmentRoundId, String q) {

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
                        userEntity.deletedAt.isNull(),
                        userEntity.role.eq(Role.USER)
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
                        assessmentRound1.deletedAt.isNull(),
                        nameContains(q)
                )
                .orderBy(
                        userEntity.id.asc(),
                        visualData.id.asc()
                )
                .fetch();
    }

    private BooleanExpression nameContains(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }
        return userEntity.name.containsIgnoreCase(q);
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

    @Override
    public List<VisualDataAssignment> findAssignmentsByUserAndAssessmentRound(Long userId, Long assessmentRoundId) {

        return queryFactory
                .selectFrom(visualDataAssignment)
                .join(visualDataAssignment.userYearRound, userYearRound)
                .join(visualDataAssignment.visualData, visualData)
                .where(
                        userYearRound.user.id.eq(userId),
                        userYearRound.assessmentRound.id.eq(assessmentRoundId),
                        userYearRound.deletedAt.isNull(),
                        visualDataAssignment.deletedAt.isNull(),
                        visualData.deletedAt.isNull()
                )
                .orderBy(visualData.id.asc())
                .fetch();
    }
}
