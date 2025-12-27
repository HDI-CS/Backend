package kr.co.hdi.domain.year.repository;

import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.year.entity.UserYearRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("""
    SELECT uyr
    FROM UserYearRound uyr
    JOIN FETCH uyr.user u
    JOIN FETCH uyr.assessmentRound ar
    JOIN FETCH ar.year
    WHERE u.userType = :type
      AND u.deletedAt IS NULL
""")
    List<UserYearRound> findAllByUserType(UserType type);
}
