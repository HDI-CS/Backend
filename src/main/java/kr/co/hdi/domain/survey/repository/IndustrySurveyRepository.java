package kr.co.hdi.domain.survey.repository;

import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndustrySurveyRepository extends JpaRepository<IndustrySurvey, Long> {

    @Query("""
    select is
    from IndustrySurvey is
    where is.deletedAt is NULL
        AND is.year.id = :yearId
    order by is.surveyType asc, is.surveyNumber asc
    """)
    List<IndustrySurvey> findAllByYear(
            @Param("yearId") Long yearId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from IndustrySurvey is
        where is.year.id = :yearId
    """)
    void deleteAllByYearId(Long yearId);

    Optional<IndustrySurvey> findBySurveyCode(String surveyCode);
}
