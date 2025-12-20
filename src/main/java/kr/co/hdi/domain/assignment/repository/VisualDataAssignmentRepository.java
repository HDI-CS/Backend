package kr.co.hdi.domain.assignment.repository;

import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.year.entity.UserYearRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}
