package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

/*
IndustryCategoryFieldSpec과 동일한 패턴. 카테고리마다 다른 스펙 필드를
VisualData.attributes(jsonb)에 자유 입력하는 대신, "이 카테고리는 이런 스펙을 가진다"를
카테고리당 한 번만 정의해두는 메타데이터.
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class VisualCategoryFieldSpec extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_category_field_spec_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private VisualDataCategory visualDataCategory;

    // null이면 해당 카테고리의 모든 연도에 공통 적용.
    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;

    private String fieldKey;

    private String fieldLabel;

    private Integer displayOrder;

    public static VisualCategoryFieldSpec create(
            VisualDataCategory category,
            Year year,
            String fieldKey,
            String fieldLabel,
            int displayOrder
    ) {
        VisualCategoryFieldSpec spec = new VisualCategoryFieldSpec();
        spec.visualDataCategory = category;
        spec.year = year;
        spec.fieldKey = fieldKey;
        spec.fieldLabel = fieldLabel;
        spec.displayOrder = displayOrder;
        return spec;
    }

    public void delete() {
        processDeletion();
    }
}
