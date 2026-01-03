package kr.co.hdi.domain.assignment.repository;

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

public interface VisualDataAssignmentRepository extends JpaRepository<VisualDataAssignment, Long>, VisualDataAssignmentRepositoryCustom {

    @Query("""
        select vda
        from VisualDataAssignment vda
        where vda.userYearRound = :userYearRound
          and vda.deletedAt is null
    """)
    List<VisualDataAssignment> findByUserYearRound(
            @Param("userYearRound") UserYearRound userYearRound
    );

    @Modifying
    @Query("""
        DELETE FROM VisualDataAssignment v
        WHERE v.userYearRound = :userYearRound
          AND v.visualData.id IN :visualDataIds
    """)
    void deleteByUserYearRoundAndVisualDataIds(
            @Param("userYearRound") UserYearRound userYearRound,
            @Param("visualDataIds") Set<Long> visualDataIds
    );

    @Query("""
        select new kr.co.hdi.domain.assignment.query.UserDataPair(
            vda.userYearRound.user.id,
            vda.visualData.id
        )
        from VisualDataAssignment vda
        JOIN vda.userYearRound uyr
        JOIN uyr.user u
        WHERE uyr.assessmentRound.id = :assessmentRoundId
        AND u.deletedAt IS NULL
    """)
    List<UserDataPair> findUserDataPairsByAssessmentRoundId(
            @Param("assessmentRoundId") Long assessmentRoundId
    );


    @Query("""
        select new kr.co.hdi.domain.assignment.query.DataIdCodePair(
            vda.visualData.id,
            vda.visualData.brandCode
        )
        from VisualDataAssignment vda
        JOIN vda.userYearRound uyr
        JOIN uyr.user u
        WHERE uyr.assessmentRound.id = :assessmentRoundId
        AND u.id = :userId
        AND u.deletedAt IS NULL
    """)
    List<DataIdCodePair> findDataIdCodePairsByAssessmentRoundIdAndUserId(
            @Param("assessmentRoundId") Long assessmentRoundId,
            @Param("userId") Long userId
    );


    @Query("""
        select new kr.co.hdi.domain.assignment.query.UserDataIdCodePair(
            vda.userYearRound.user.id,
            vda.userYearRound.user.name,
            vda.visualData.id,
            vda.visualData.brandCode
        )
        from VisualDataAssignment vda
        JOIN vda.userYearRound uyr
        WHERE uyr.assessmentRound.id = :assessmentRoundId
        AND vda.deletedAt IS NULL
    """)
    List<UserDataIdCodePair> findDataIdCodePairsByAssessmentRoundId(
            @Param("assessmentRoundId") Long assessmentRoundId
    );

    Optional<VisualDataAssignment> findByUserYearRoundIdAndVisualDataId(Long userYearRoundId, Long visualDataId);
}
