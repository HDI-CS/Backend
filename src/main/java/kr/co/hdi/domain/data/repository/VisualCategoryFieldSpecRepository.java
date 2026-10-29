package kr.co.hdi.domain.data.repository;

import kr.co.hdi.domain.data.entity.VisualCategoryFieldSpec;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisualCategoryFieldSpecRepository extends JpaRepository<VisualCategoryFieldSpec, Long> {

    @Query("""
        select s
        from VisualCategoryFieldSpec s
        where s.visualDataCategory = :category
          and s.year.id = :yearId
          and s.deletedAt is null
        order by s.displayOrder asc, s.id asc
    """)
    List<VisualCategoryFieldSpec> findByCategoryAndYear(
            @Param("category") VisualDataCategory category,
            @Param("yearId") Long yearId
    );

    @Query("""
        select s
        from VisualCategoryFieldSpec s
        where s.visualDataCategory = :category
          and s.year is null
          and s.deletedAt is null
        order by s.displayOrder asc, s.id asc
    """)
    List<VisualCategoryFieldSpec> findCommonByCategory(
            @Param("category") VisualDataCategory category
    );

    @Query("""
        select s
        from VisualCategoryFieldSpec s
        where s.visualDataCategory = :category
          and s.deletedAt is null
        order by s.displayOrder asc, s.id asc
    """)
    List<VisualCategoryFieldSpec> findAllByCategory(
            @Param("category") VisualDataCategory category
    );

    @Query("""
        select max(s.displayOrder)
        from VisualCategoryFieldSpec s
        where s.visualDataCategory = :category
          and ((:yearId is null and s.year is null) or s.year.id = :yearId)
          and s.deletedAt is null
    """)
    Integer findMaxDisplayOrder(
            @Param("category") VisualDataCategory category,
            @Param("yearId") Long yearId
    );

    @Query("""
        select count(s) > 0
        from VisualCategoryFieldSpec s
        where s.visualDataCategory = :category
          and ((:yearId is null and s.year is null) or s.year.id = :yearId)
          and s.fieldKey = :fieldKey
          and s.deletedAt is null
    """)
    boolean existsByCategoryAndYearAndFieldKey(
            @Param("category") VisualDataCategory category,
            @Param("yearId") Long yearId,
            @Param("fieldKey") String fieldKey
    );
}
