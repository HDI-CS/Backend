package kr.co.hdi.domain.assignment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.admin.assignment.dto.query.AssignmentRow;
import kr.co.hdi.domain.year.enums.DomainType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static kr.co.hdi.domain.assignment.entity.QIndustryDataAssignment.industryDataAssignment;
import static kr.co.hdi.domain.assignment.entity.QVisualDataAssignment.visualDataAssignment;
import static kr.co.hdi.domain.data.entity.QIndustryData.industryData;
import static kr.co.hdi.domain.user.entity.QUserEntity.userEntity;
import static kr.co.hdi.domain.year.entity.QAssessmentRound.assessmentRound1;
import static kr.co.hdi.domain.year.entity.QUserYearRound.userYearRound;

@RequiredArgsConstructor
public class IndustryDataAssignmentRepositoryImpl implements IndustryDataAssignmentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public LocalDateTime findLastModifiedAtByAssessmentRound(Long assessmentRoundId) {
        return queryFactory
                .select(industryDataAssignment.updatedAt.max())
                .from(industryDataAssignment)
                .join(industryDataAssignment.userYearRound, userYearRound)
                .join(userYearRound.assessmentRound, assessmentRound1)
                .where(
                        assessmentRound1.id.eq(assessmentRoundId),
                        assessmentRound1.deletedAt.isNull(),
                        industryDataAssignment.deletedAt.isNull()
                )
                .fetchOne();
    }

    @Override
    public List<AssignmentRow> findIndustryDataAssignment(Long assessmentRoundId, String q) {

        return queryFactory
                .select(Projections.constructor(
                        AssignmentRow.class,
                        userEntity.id,
                        userEntity.name,
                        industryData.id,
                        industryData.originalId
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
                .join(industryDataAssignment).on(
                        industryDataAssignment.userYearRound.eq(userYearRound),
                        industryDataAssignment.deletedAt.isNull()
                )
                .join(industryData).on(
                        industryDataAssignment.industryData.eq(industryData),
                        industryData.deletedAt.isNull()
                )
                .where(
                        assessmentRound1.id.eq(assessmentRoundId),
                        assessmentRound1.domainType.eq(DomainType.INDUSTRY),
                        assessmentRound1.deletedAt.isNull(),
                        nameContains(q)
                )
                .orderBy(
                        userEntity.id.asc(),
                        industryData.id.asc()
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
    public List<AssignmentRow> findIndustryDataAssignmentByUser(Long assessmentRoundId, Long userId) {

        return queryFactory
                .select(Projections.constructor(
                        AssignmentRow.class,
                        userEntity.id,
                        userEntity.name,
                        industryData.id,
                        industryData.originalId
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
                .join(industryDataAssignment).on(
                        industryDataAssignment.userYearRound.eq(userYearRound),
                        industryDataAssignment.deletedAt.isNull()
                )
                .join(industryData).on(
                        industryDataAssignment.industryData.eq(industryData),
                        industryData.deletedAt.isNull()
                )
                .where(
                        userEntity.id.eq(userId),
                        assessmentRound1.id.eq(assessmentRoundId),
                        assessmentRound1.domainType.eq(DomainType.INDUSTRY),
                        assessmentRound1.deletedAt.isNull()
                )
                .orderBy(
                        userEntity.id.asc(),
                        industryData.id.asc()
                )
                .fetch();
    }
}
