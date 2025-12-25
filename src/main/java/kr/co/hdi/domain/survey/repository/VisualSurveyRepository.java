package kr.co.hdi.domain.survey.repository;

import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisualSurveyRepository extends JpaRepository<VisualSurvey, Long> {

    @Query("""
    select vs
    from VisualSurvey vs
    where vs.deletedAt is NULL
        AND vs.year.id = :yearId
    """)
    VisualSurvey findByYearId(Long yearId);
}
