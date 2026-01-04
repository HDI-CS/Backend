package kr.co.hdi.domain.currentSurvey.repository;

import kr.co.hdi.domain.currentSurvey.entity.CurrentIndustryCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentIndustryCategoryRepository extends JpaRepository<CurrentIndustryCategory, Long> {
}
