package kr.co.hdi.dataset.repository;

import kr.co.hdi.dataset.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByBrandCode(String brandCode);
}
