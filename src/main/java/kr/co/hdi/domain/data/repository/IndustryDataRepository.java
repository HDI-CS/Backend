package kr.co.hdi.domain.data.repository;

import kr.co.hdi.admin.data.dto.response.IndustryDataIdsResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataIdsResponse;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.entity.VisualData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndustryDataRepository extends JpaRepository<IndustryData, Long> {

    @Query("""
    SELECT i
    FROM IndustryData i
    WHERE i.deletedAt IS NULL
        AND i.year.id = :yearId
    """)
    List<IndustryData> findByYearIdAndDeletedAtIsNull(@Param("yearId") Long yearId);

    @Query("""
    SELECT new kr.co.hdi.admin.data.dto.response.IndustryDataIdsResponse(
        i.id,
        i.originalId)
    FROM IndustryData i
    WHERE i.deletedAt IS NULL
        AND i.year.id = :yearId
    """)
    List<IndustryDataIdsResponse> findIdByYearId(@Param("yearId") Long yearId);

    Optional<IndustryData> findByIdAndDeletedAtIsNull(Long id);

    List<IndustryData> findByIdInAndDeletedAtIsNull(List<Long> ids);
}
