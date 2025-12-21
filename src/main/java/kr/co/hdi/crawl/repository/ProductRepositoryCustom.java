package kr.co.hdi.crawl.repository;

import kr.co.hdi.crawl.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepositoryCustom extends JpaRepository<Product, Long>, kr.co.hdi.crawl.repository.query.ProductRepositoryCustom {

    Optional<Product> findByOriginalId(Long originalId);
}
