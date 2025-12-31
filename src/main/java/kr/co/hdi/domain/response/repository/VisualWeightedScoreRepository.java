package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.VisualWeightedScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisualWeightedScoreRepository extends JpaRepository<VisualWeightedScore, Long> {
    @Query("""
    select vws
    from VisualWeightedScore vws
    JOIN FETCH vws.userYearRound uyr
    JOIN FETCH uyr.user u
    WHERE uyr.assessmentRound.id = :assessmentRoundId
    AND u.deletedAt IS NULL
    order by vws.id asc
    """)
    List<VisualWeightedScore> findAllByUserYearRound(
            @Param("assessmentRoundId") Long assessmentRoundId
    );
}
