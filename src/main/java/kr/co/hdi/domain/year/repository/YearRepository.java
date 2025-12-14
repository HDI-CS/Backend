package kr.co.hdi.domain.year.repository;

import kr.co.hdi.domain.year.entity.Year;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearRepository extends JpaRepository<Year, Long> {
}
