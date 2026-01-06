package kr.co.hdi.domain.year.repository;

import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.enums.DomainType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface YearRepository extends JpaRepository<Year, Long> {

    Optional<Year> findByIdAndDeletedAtIsNull(Long yearId);

    List<Year> findAllByTypeAndDeletedAtIsNull(DomainType type);

    List<Year> findAllByTypeAndDeletedAtIsNullOrderByCreatedAtAsc(DomainType type);
}
