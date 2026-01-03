package kr.co.hdi.admin.survey.service;
import kr.co.hdi.admin.survey.dto.request.SurveyContentResquest;
import kr.co.hdi.admin.survey.dto.request.SurveyDateRequest;
import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
import kr.co.hdi.admin.survey.dto.response.SurveyQuestionsByYearResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyRoundIdResponse;
import kr.co.hdi.admin.survey.dto.response.SurveyYearIdResponse;
import kr.co.hdi.domain.year.enums.DomainType;

import java.util.List;

public interface SurveyService {

    DomainType getDomainType();
    List<SurveyResponse> getSurveys(DomainType type);
    SurveyYearIdResponse createSurvey(DomainType type);
    public void updateYearFolderName(DomainType type, Long yearId, String newFolderName);
    public void updateRoundFolderName(DomainType type, Long assessmentRoundId, String newFolderName);
    public SurveyRoundIdResponse createRound(DomainType type, Long yearId);
    public void updateSurveyContent(DomainType type, List<SurveyContentResquest> requests);
    public void upsertSurveyDate(DomainType type, Long assessmentRoundId, SurveyDateRequest request);
    public void createSurveyQuestion(DomainType type, Long yearId, List<SurveyQuestionRequest> request);
    public SurveyQuestionsByYearResponse getSurveyQuestions(DomainType type, Long yearId);
}
