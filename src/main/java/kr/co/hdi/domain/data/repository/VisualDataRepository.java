package kr.co.hdi.domain.data.repository;

import kr.co.hdi.admin.data.dto.response.VisualDataIdsResponse;
import kr.co.hdi.domain.data.entity.VisualData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisualDataRepository extends JpaRepository<VisualData, Long> {

    @Query("""
    SELECT v
    FROM VisualData v
    WHERE v.deletedAt IS NULL
        AND v.year.id = :yearId
    """)
    List<VisualData> findByYearIdAndDeletedAtIsNull(@Param("yearId") Long yearId);

    @Query("""
    SELECT new kr.co.hdi.admin.data.dto.response.VisualDataIdsResponse(
        v.id,
        v.brandCode)
    FROM VisualData v
    WHERE v.deletedAt IS NULL
        AND v.year.id = :yearId
    """)
    List<VisualDataIdsResponse> findIdByYearId(@Param("yearId") Long yearId);
}
