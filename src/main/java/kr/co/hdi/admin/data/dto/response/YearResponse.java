package kr.co.hdi.admin.data.dto.response;

import kr.co.hdi.domain.year.entity.Year;

import java.time.LocalDateTime;

public record YearResponse(
        Long yearId,
        String folderName,
        LocalDateTime updatedAt,
        LocalDateTime createdAt
) {

    public static YearResponse from(Year year, LocalDateTime updatedAt) {
        return new YearResponse(
                year.getId(),
                year.getYear(),
                updatedAt,
                year.getCreatedAt()
        );
    }
}
