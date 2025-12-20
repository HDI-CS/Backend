package kr.co.hdi.domain.year.repository;

import kr.co.hdi.domain.year.entity.UserYearRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserYearRoundRepository extends JpaRepository<UserYearRound, Long> {

    @Query("""
        select uyr
        from UserYearRound uyr
        where uyr.assessmentRound.id = :assessmentRoundId
          and uyr.user.id = :userId
          and uyr.deletedAt is null
    """)
    Optional<UserYearRound> findByAssessmentRoundIdAndUserId(
            @Param("assessmentRoundId") Long assessmentRoundId,
            @Param("userId") Long userId
    );
}
