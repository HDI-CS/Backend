package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.domain.assignment.entity.IndustryDataAssignment;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.assignment.query.DataIdCodePair;
import kr.co.hdi.domain.assignment.query.UserDataIdCodePair;
import kr.co.hdi.domain.assignment.query.UserDataPair;
import kr.co.hdi.domain.year.entity.UserYearRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IndustryDataAssignmentRepository extends JpaRepository<IndustryDataAssignment, Long>, IndustryDataAssignmentRepositoryCustom {

    @Query("""
        select ida
        from IndustryDataAssignment ida
        where ida.userYearRound = :userYearRound
          and ida.deletedAt is null
    """)
    List<IndustryDataAssignment> findByUserYearRound(
            @Param("userYearRound") UserYearRound userYearRound
    );

    @Modifying
    @Query("""
        DELETE FROM IndustryDataAssignment i
        WHERE i.userYearRound = :userYearRound
          AND i.industryData.id IN :industryDataIds
    """)
    void deleteByUserYearRoundAndIndustryDataIds(
            @Param("userYearRound") UserYearRound userYearRound,
            @Param("industryDataIds") Set<Long> industryDataIds
    );

    @Query("""
        select new kr.co.hdi.domain.assignment.query.UserDataPair(
            ida.userYearRound.user.id,
            ida.industryData.id
        )
        from IndustryDataAssignment ida
        JOIN ida.userYearRound uyr
        JOIN uyr.user u
        WHERE uyr.assessmentRound.id = :assessmentRoundId
        AND ida.deletedAt IS NULL
    """)
    List<UserDataPair> findUserDataPairsByAssessmentRoundId(
            @Param("assessmentRoundId") Long assessmentRoundId
    );

    @Query("""
        select new kr.co.hdi.domain.assignment.query.DataIdCodePair(
            ida.industryData.id,
            ida.industryData.originalId
        )
        from IndustryDataAssignment ida
        JOIN ida.userYearRound uyr
        JOIN uyr.user u
        WHERE uyr.assessmentRound.id = :assessmentRoundId
        AND u.id = :userId
        AND ida.deletedAt IS NULL
        ORDER BY ida.industryData.originalIdInteger
    """)
    List<DataIdCodePair> findDataIdCodePairsByAssessmentRoundIdAndUserId(
            @Param("assessmentRoundId") Long assessmentRoundId,
            @Param("userId") Long userId
    );

    @Query("""
        select new kr.co.hdi.domain.assignment.query.UserDataIdCodePair(
            ida.userYearRound.user.id,
            ida.userYearRound.user.name,
            ida.industryData.id,
            ida.industryData.originalId
        )
        from IndustryDataAssignment ida
        JOIN ida.userYearRound uyr
        WHERE uyr.assessmentRound.id = :assessmentRoundId
        AND ida.deletedAt IS NULL
        Order by ida.industryData.originalId
    """)
    List<UserDataIdCodePair> findDataIdCodePairsByAssessmentRoundId(
            @Param("assessmentRoundId") Long assessmentRoundId
    );

    Optional<IndustryDataAssignment> findByUserYearRoundIdAndIndustryDataId(Long userYearRoundId, Long industryDataId);
}
