package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.assignment.entity.IndustryDataAssignment;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.survey.domain.ResponseStatus;

public record ProductSurveyDataResponse(

        Long dataId,
        String name,
        String image,
        ResponseStatus responseStatus
) {

    public static ProductSurveyDataResponse toResponseDto(VisualDataAssignment assignment) {

        Integer surveyCount = assignment.getSurveyCount();
        Integer responseCount = assignment.getResponseCount();

        ResponseStatus status;
        if (responseCount == null || responseCount == 0) {
            status = ResponseStatus.NOT_STARTED;
        } else if (surveyCount != null && responseCount >= surveyCount) {
            status = ResponseStatus.DONE;
        } else {
            status = ResponseStatus.IN_PROGRESS;
        }

        return new ProductSurveyDataResponse(
                assignment.getVisualData().getId(),
                assignment.getVisualData().getBrandName(),
                assignment.getVisualData().getLogoImage(),
                status
        );
    }

    public static ProductSurveyDataResponse toResponseDto(IndustryDataAssignment assignment) {

        Integer surveyCount = assignment.getSurveyCount();
        Integer responseCount = assignment.getResponseCount();

        ResponseStatus status;
        if (responseCount == null || responseCount == 0) {
            status = ResponseStatus.NOT_STARTED;
        } else if (surveyCount != null && responseCount >= surveyCount) {
            status = ResponseStatus.DONE;
        } else {
            status = ResponseStatus.IN_PROGRESS;
        }

        return new ProductSurveyDataResponse(
                assignment.getIndustryData().getId(),
                assignment.getIndustryData().getProductName(),
                assignment.getIndustryData().getFrontImagePath(),  // 산디 정면 이미지
                status
        );
    }
}
