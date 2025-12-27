package kr.co.hdi.domain.survey.repository;

import kr.co.hdi.domain.survey.entity.IndustrySurvey;
import kr.co.hdi.domain.survey.entity.VisualSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisualSurveyRepository extends JpaRepository<VisualSurvey, Long> {

    @Query("""
    select vs
    from VisualSurvey vs
    where vs.deletedAt is NULL
        AND vs.year.id = :yearId
    order by vs.surveyType asc, vs.surveyNumber asc
    """)
    List<VisualSurvey> findAllByYear(@Param("yearId") Long yearId);
}
