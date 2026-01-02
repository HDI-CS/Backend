package kr.co.hdi.domain.year.repository;

import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssessmentRoundRepository extends JpaRepository<AssessmentRound, Long> {

    @Query("""
    select ar
    from AssessmentRound ar
    where ar.deletedAt is NULL
        AND ar.year.id = :yearId
        AND ar.domainType = :domainType
    """)
    List<AssessmentRound> findByDomainTypeAndYear(
            @Param("domainType") DomainType domainType,
            @Param("yearId") Long yearId);

    @Query("""
    select ar
    from AssessmentRound ar
    join fetch ar.year y
    where ar.deletedAt is NULL
        AND ar.domainType = :type
    order by y.id asc, ar.id asc
    """)
    List<AssessmentRound> findAllWithYearByDomainType(
            @Param("type") DomainType type);

    @Query("""
        select ar.year.surveyCount
        from AssessmentRound ar
        where ar.id = :assessmentRoundId
    """)
    Integer findSurveyCountByAssessmentRoundId(
            @Param("assessmentRoundId") Long assessmentRoundId
    );

    @Query("""
        SELECT ar
        FROM AssessmentRound ar
        JOIN FETCH ar.year
        WHERE ar.id = :assessmentRoundId
    """)
    Optional<AssessmentRound> findByIdWithYear(
            @Param("assessmentRoundId") Long assessmentRoundId
    );
}
