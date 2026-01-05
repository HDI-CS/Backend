package kr.co.hdi.domain.year.repository;

import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.year.entity.AssessmentRound;
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

    @Query("""
    SELECT u
    FROM UserYearRound uyr
    JOIN uyr.user u
    JOIN uyr.assessmentRound ar
    WHERE u.userType = :userType
      AND ar = :assessmentRound
      AND u.deletedAt IS NULL
""")
    List<UserEntity> findUsers(
            @Param("userType") UserType userType,
            @Param("assessmentRound") AssessmentRound assessmentRound
    );

    @Query("""
    SELECT u
    FROM UserYearRound uyr
    JOIN uyr.user u
    JOIN uyr.assessmentRound ar
    WHERE u.userType = :userType
      AND ar = :assessmentRound
      AND u.deletedAt IS NULL
      AND u.name like concat('%', :q, '%')
""")
    List<UserEntity> findUsersBySearch(
            @Param("userType") UserType userType,
            @Param("assessmentRound") AssessmentRound assessmentRound,
            @Param("q") String q
    );
}
