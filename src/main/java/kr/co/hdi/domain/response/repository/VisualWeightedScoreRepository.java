package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.VisualWeightedScore;
import kr.co.hdi.domain.response.query.UserVisualWeightedScorePair;
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
    select new kr.co.hdi.domain.response.query.UserVisualWeightedScorePair(
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
    List<UserVisualWeightedScorePair> findPairsByUserYearRound(
            @Param("assessmentRoundId") Long assessmentRoundId
    );

    List<VisualWeightedScore> findAllByUserYearRoundId(Long userYearRoundId);

    @Query("""
    select vws
    from VisualWeightedScore vws
    join fetch vws.userYearRound uyr
    join fetch uyr.user u
    join uyr.assessmentRound ar
    where ar.year.id = :yearId
      and vws.deletedAt is null
      and u.deletedAt is null
""")
    List<VisualWeightedScore> findAllByYearId(@Param("yearId") Long yearId);
}
