package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.response.entity.VisualResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IndustryResponseRepository extends JpaRepository<IndustryResponse, Long> {

    @Query("""
    select ir
    from IndustryResponse ir
    JOIN FETCH ir.userYearRound uyr
    JOIN FETCH uyr.user u
    JOIN FETCH ir.industryData
    WHERE uyr.assessmentRound.id = :assessmentRoundId
    AND ir.deletedAt IS NULL
    order by ir.id asc
    """)
    List<IndustryResponse> findAllByUserYearRound(
            @Param("assessmentRoundId") Long assessmentRoundId
    );

    @Query("""
    select ir
    from IndustryResponse ir
    JOIN FETCH ir.industryData
    WHERE ir.userYearRound.assessmentRound.id = :assessmentRoundId
    AND ir.userYearRound.user.id = :userId
    AND ir.deletedAt IS NULL
    order by ir.id asc
    """)
    List<IndustryResponse> findAllByAssessmentRoundIdAndMemberId(
            @Param("assessmentRoundId") Long assessmentRoundId,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT ir
        FROM IndustryResponse ir
        JOIN ir.userYearRound uyr
        WHERE ir.industryData.id = :dataId
          AND uyr.user.id = :userId
        """)
    List<IndustryResponse> findAllByIndustryDataIdAndUserId(@Param("dataId") Long dataId, @Param("userId") Long userId);

    Optional<IndustryResponse> findByUserYearRoundIdAndIndustrySurveyIdAndIndustryDataId(
            Long userYearRoundId,
            Long industrySurveyId,
            Long industryDataId
    );
}
