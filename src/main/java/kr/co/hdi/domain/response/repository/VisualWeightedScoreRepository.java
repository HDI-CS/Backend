package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.VisualWeightedScore;
import kr.co.hdi.domain.response.query.UserWeightedScorePair;
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

    @Query("""
    select new kr.co.hdi.domain.response.query.UserWeightedScorePair(
            vws.userYearRound.user.id,
            vws.userYearRound.user.name,
            vws.score1,
            vws.score2,
            vws.score3,
            vws.score4,
            vws.score5,
            vws.score6,
            vws.score7,
            vws.score8,
            vws.visualDataCategory
       )
    from VisualWeightedScore vws
    WHERE vws.userYearRound.assessmentRound.id = :assessmentRoundId
    AND vws.deletedAt IS NULL
    order by vws.id asc
    """)
    List<UserWeightedScorePair> findParisByUserYearRound(
            @Param("assessmentRoundId") Long assessmentRoundId
    );
}
