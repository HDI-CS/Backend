package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.VisualResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisualResponseRepository extends JpaRepository<VisualResponse, Long> {

    @Query("""
    select vr
    from VisualResponse vr
    JOIN FETCH vr.visualData
    WHERE vr.userYearRound.assessmentRound.id = :assessmentRoundId
    AND vr.userYearRound.user.id = :userId
    AND vr.deletedAt IS NULL
    order by vr.id asc
    """)
    List<VisualResponse> findAllByAssessmentRoundIdAndMemberId(
            @Param("assessmentRoundId") Long assessmentRoundId,
            @Param("userId") Long memberId
    );

    @Query("""
    select vr
    from VisualResponse vr
    JOIN FETCH vr.userYearRound uyr
    JOIN FETCH uyr.user u
    JOIN FETCH vr.visualData
    WHERE uyr.assessmentRound.id = :assessmentRoundId
    AND vr.deletedAt IS NULL
    order by vr.id asc
    """)
    List<VisualResponse> findAllByUserYearRound(
            @Param("assessmentRoundId") Long assessmentRoundId
    );
}
