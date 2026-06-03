package kr.co.hdi.survey.dto.request;

import java.util.List;

public record SurveySubmitAllRequest (
    List<SurveyResponseRequest> responses
){}
