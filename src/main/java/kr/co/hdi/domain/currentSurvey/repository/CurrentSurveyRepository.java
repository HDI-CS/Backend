package kr.co.hdi.domain.currentSurvey.repository;

import kr.co.hdi.domain.currentSurvey.entity.CurrentSurvey;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrentSurveyRepository extends JpaRepository<CurrentSurvey, Long> {

    Optional<CurrentSurvey> findByDomainType(DomainType domainType);
}
