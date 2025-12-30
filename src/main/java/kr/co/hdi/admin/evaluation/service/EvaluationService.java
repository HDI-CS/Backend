package kr.co.hdi.admin.evaluation.service;
import kr.co.hdi.admin.evaluation.dto.response.EvaluationStatusByMemberResponse;
import kr.co.hdi.admin.survey.dto.request.SurveyDateRequest;
import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
import kr.co.hdi.admin.survey.dto.response.SurveyQuestionsByYearResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyRoundIdResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyYearIdResponse;
import kr.co.hdi.domain.year.enums.DomainType;

import java.util.List;

public interface EvaluationService {

    DomainType getDomainType();
    List<EvaluationStatusByMemberResponse> getEvaluationStatus(
            DomainType type,
            Long assessmentRoundId
    );
}
