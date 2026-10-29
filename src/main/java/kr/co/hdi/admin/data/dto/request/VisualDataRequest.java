package kr.co.hdi.admin.data.dto.request;

import kr.co.hdi.domain.data.enums.VisualDataCategory;

import java.util.Map;

public record VisualDataRequest(
        String code,
        String name,
        String sectorCategory,
        String referenceUrl,

        // 카테고리마다 다른 스펙 필드 (고정 컬럼이 아닌 key-value)
        Map<String, String> attributes,

        String originalLogoImage,
        VisualDataCategory visualDataCategory
) {
}
