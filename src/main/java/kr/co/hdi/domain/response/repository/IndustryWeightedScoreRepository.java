package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.IndustryWeightedScore;
import kr.co.hdi.domain.response.query.UserIndustryWeightedScorePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndustryWeightedScoreRepository extends JpaRepository<IndustryWeightedScore, Long> {
    @Query("""
    select iws
    from IndustryWeightedScore iws
    JOIN FETCH iws.userYearRound uyr
    JOIN FETCH uyr.user u
    WHERE uyr.assessmentRound.id = :assessmentRoundId
    AND u.deletedAt IS NULL
    order by iws.id asc
    """)
    List<IndustryWeightedScore> findAllByUserYearRound(
            @Param("assessmentRoundId") Long assessmentRoundId
    );

    List<IndustryWeightedScore> findAllByUserYearRoundId(Long userYearRoundId);


    @Query("""
    select new kr.co.hdi.domain.response.query.UserIndustryWeightedScorePair(
            iws.userYearRound.user.id,
            iws.userYearRound.user.name,
            iws.score1,
            iws.score2,
            iws.score3,
            iws.score4,
            iws.score5,
            iws.score6,
            iws.score7,
            iws.score8,
            iws.industryDataCategory
       )
    from IndustryWeightedScore iws
    WHERE iws.userYearRound.assessmentRound.id = :assessmentRoundId
    AND iws.deletedAt IS NULL
    order by iws.id asc
    """)
    List<UserIndustryWeightedScorePair> findParisByUserYearRound(
            @Param("assessmentRoundId") Long assessmentRoundId
    );
}
