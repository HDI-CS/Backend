package kr.co.hdi.domain.survey.repository;

import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
