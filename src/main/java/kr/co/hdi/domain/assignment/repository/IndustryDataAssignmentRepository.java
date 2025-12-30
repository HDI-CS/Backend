package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.domain.assignment.entity.IndustryDataAssignment;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.assignment.query.UserDataPair;
import kr.co.hdi.domain.year.entity.UserYearRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
            @Param("visualDataIds") Set<Long> industryDataIds
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
        AND u.deletedAt IS NULL
    """)
    List<UserDataPair> findUserDataPairsByAssessmentRoundId(
            @Param("assessmentRoundId") Long assessmentRoundId
    );
}
