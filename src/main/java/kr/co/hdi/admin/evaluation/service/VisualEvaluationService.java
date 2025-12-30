//package kr.co.hdi.admin.evaluation.service;
//
//import kr.co.hdi.admin.survey.dto.request.SurveyDateRequest;
//import kr.co.hdi.admin.survey.dto.request.SurveyQuestionRequest;
//import kr.co.hdi.admin.survey.dto.response.*;
//import kr.co.hdi.admin.survey.exception.SurveyErrorCode;
//import kr.co.hdi.admin.survey.exception.SurveyException;
//import kr.co.hdi.domain.survey.entity.VisualSurvey;
//import kr.co.hdi.domain.survey.enums.SurveyType;
//import kr.co.hdi.domain.survey.repository.VisualSurveyRepository;
//import kr.co.hdi.domain.year.entity.AssessmentRound;
//import kr.co.hdi.domain.year.entity.Year;
//import kr.co.hdi.domain.year.enums.DomainType;
//import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
//import kr.co.hdi.domain.year.repository.YearRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class VisualEvaluationService implements EvaluationService {
//
//    @Override
//    public DomainType getDomainType() {
//        return DomainType.VISUAL;
//    }
//
//}
