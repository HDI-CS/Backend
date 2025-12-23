package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.year.entity.UserYearRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface VisualDataAssignmentRepository extends JpaRepository<VisualDataAssignment, Long>, VisualDataAssignmentRepositoryCustom {

    @Query("""
        select vda
        from VisualDataAssignment vda
        where vda.userYearRound = :userYearRound
          and vda.deletedAt is null
    """)
    List<VisualDataAssignment> findByUserYearRound(
            @Param("userYearRound") UserYearRound userYearRound
    );

    @Modifying
    @Query("""
        DELETE FROM VisualDataAssignment v
        WHERE v.userYearRound = :userYearRound
          AND v.visualData.id IN :visualDataIds
    """)
    void deleteByUserYearRoundAndVisualDataIds(
            @Param("userYearRound") UserYearRound userYearRound,
            @Param("visualDataIds") Set<Long> visualDataIds
    );
}
