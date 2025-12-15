package kr.co.hdi.admin.data.dto.response;

import kr.co.hdi.domain.year.entity.Year;

public record YearResponse(
        Long id,
        Integer year
) {

    public static YearResponse from(Year year) {
        return new YearResponse(
                year.getId(),
                year.getYear()
        );
    }
}
