package kr.co.hdi.survey.dto.response;

import kr.co.hdi.domain.assignment.entity.IndustryDataAssignment;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.survey.domain.ResponseStatus;

public record SurveyDataPreviewResponse(

        Long dataId,
        String name,
        String image,
        ResponseStatus responseStatus
) {

    public static SurveyDataPreviewResponse toResponseDto(VisualDataAssignment assignment, Integer surveyCount, String image) {

        Integer responseCount = assignment.getResponseCount();

        ResponseStatus status;
        if (responseCount == null || responseCount == 0) {
            status = ResponseStatus.NOT_STARTED;
        } else if (surveyCount != null && responseCount >= surveyCount) {
            status = ResponseStatus.DONE;
        } else {
            status = ResponseStatus.IN_PROGRESS;
        }

        return new SurveyDataPreviewResponse(
                assignment.getVisualData().getId(),
                assignment.getVisualData().getBrandName(),
                image,
                status
        );
    }

    public static SurveyDataPreviewResponse toResponseDto(IndustryDataAssignment assignment, Integer surveyCount, String image) {

        Integer responseCount = assignment.getResponseCount();

        ResponseStatus status;
        if (responseCount == null || responseCount == 0) {
            status = ResponseStatus.NOT_STARTED;
        } else if (surveyCount != null && responseCount >= surveyCount) {
            status = ResponseStatus.DONE;
        } else {
            status = ResponseStatus.IN_PROGRESS;
        }

        return new SurveyDataPreviewResponse(
                assignment.getIndustryData().getId(),
                assignment.getIndustryData().getProductName(),
                image,  // 산디 정면 이미지
                status
        );
    }
}
