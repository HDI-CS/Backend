package kr.co.hdi.admin.survey.dto.request;
import java.time.LocalDate;

public record SurveyDateRequest(
        LocalDate startDate,
        LocalDate endDate
) {
}
