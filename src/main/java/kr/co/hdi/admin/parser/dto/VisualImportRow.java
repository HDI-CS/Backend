package kr.co.hdi.admin.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VisualImportRow {

    private final String code;
    private final String sectorCategory;
    private final String title;
    private final String releaseYear;
    private final String country;
    private final String clientName;
    private final String contentType;
    private final String visualType;
    private final String designDescription;
    private final String originalLogoImage;
    private final String referenceUrl;
}