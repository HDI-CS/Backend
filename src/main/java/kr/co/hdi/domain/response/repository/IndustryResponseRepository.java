package kr.co.hdi.domain.response.repository;

import kr.co.hdi.domain.response.entity.IndustryResponse;
import kr.co.hdi.domain.response.entity.VisualResponse;
import kr.co.hdi.domain.response.query.UserResponsePair;
import kr.co.hdi.domain.response.query.UserSurveyResponsePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IndustryResponseRepository extends JpaRepository<IndustryResponse, Long> {

    @Query("""
    select new kr.co.hdi.domain.response.query.UserSurveyResponsePair(
            ir.userYearRound.user.id,
            ir.industryData.id,
            ir.industrySurvey.surveyNumber,
            ir.numberResponse,
            ir.textResponse
       )
    from IndustryResponse ir
    WHERE ir.userYearRound.assessmentRound.id = :assessmentRoundId
    AND ir.deletedAt IS NULL
    order by ir.id asc
    """)
    List<UserSurveyResponsePair> findAllByUserYearRound(
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

    @Query("""
    select new kr.co.hdi.domain.response.query.UserResponsePair(
            ir.userYearRound.user.id,
            ir.industryData.id,
            ir.numberResponse,
            ir.textResponse
       )
    from IndustryResponse ir
    WHERE ir.userYearRound.assessmentRound.id = :assessmentRoundId
    AND ir.deletedAt IS NULL
    order by ir.id asc
    """)
    List<UserResponsePair> findPairsByUserYearRound(Long assessmentRoundId);
}
