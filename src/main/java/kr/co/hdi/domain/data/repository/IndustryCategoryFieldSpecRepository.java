package kr.co.hdi.domain.data.repository;

import kr.co.hdi.domain.data.entity.IndustryCategoryFieldSpec;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IndustryCategoryFieldSpecRepository extends JpaRepository<IndustryCategoryFieldSpec, Long> {

    @Query("""
        select s
        from IndustryCategoryFieldSpec s
        where s.industryDataCategory = :category
          and s.year.id = :yearId
          and s.deletedAt is null
        order by s.displayOrder asc, s.id asc
    """)
    List<IndustryCategoryFieldSpec> findByCategoryAndYear(
            @Param("category") IndustryDataCategory category,
            @Param("yearId") Long yearId
    );

    @Query("""
        select s
        from IndustryCategoryFieldSpec s
        where s.industryDataCategory = :category
          and s.year is null
          and s.deletedAt is null
        order by s.displayOrder asc, s.id asc
    """)
    List<IndustryCategoryFieldSpec> findCommonByCategory(
            @Param("category") IndustryDataCategory category
    );

    // 평가자 화면은 연도 구분 없이 "이 카테고리에 어떤 키가 어떤 라벨인지"만 필요하므로
    // year 필터 없이 카테고리의 모든 스펙(연도 전용 + 공통)을 합쳐서 조회.
    @Query("""
        select s
        from IndustryCategoryFieldSpec s
        where s.industryDataCategory = :category
          and s.deletedAt is null
        order by s.displayOrder asc, s.id asc
    """)
    List<IndustryCategoryFieldSpec> findAllByCategory(
            @Param("category") IndustryDataCategory category
    );

    @Query("""
        select max(s.displayOrder)
        from IndustryCategoryFieldSpec s
        where s.industryDataCategory = :category
          and ((:yearId is null and s.year is null) or s.year.id = :yearId)
          and s.deletedAt is null
    """)
    Integer findMaxDisplayOrder(
            @Param("category") IndustryDataCategory category,
            @Param("yearId") Long yearId
    );

    // 같은 (카테고리, 연도, 키) 조합으로 이미 정의된 스펙이 있는지 확인 — 중복 생성 방지용
    @Query("""
        select count(s) > 0
        from IndustryCategoryFieldSpec s
        where s.industryDataCategory = :category
          and ((:yearId is null and s.year is null) or s.year.id = :yearId)
          and s.fieldKey = :fieldKey
          and s.deletedAt is null
    """)
    boolean existsByCategoryAndYearAndFieldKey(
            @Param("category") IndustryDataCategory category,
            @Param("yearId") Long yearId,
            @Param("fieldKey") String fieldKey
    );
}
