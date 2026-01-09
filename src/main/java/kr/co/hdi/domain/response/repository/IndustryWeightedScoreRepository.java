package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.IndustryWeightedScore;
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
}
