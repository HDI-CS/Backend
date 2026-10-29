package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

/*
카테고리마다 다른 스펙 필드를 IndustryData.attributes(jsonb)에 자유 입력으로 넣다 보니
제품마다 같은 키 이름을 매번 다시 입력해야 하는 문제가 있었다 (오타로 키가 갈라질 위험도 있음).
이 엔티티는 "이 카테고리는 이런 스펙을 가진다"를 카테고리당 한 번만 정의해두는 용도.
실제 값은 여전히 IndustryData.attributes에 들어가고, 여긴 "어떤 키/라벨이 있는지"만 가진다.
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class IndustryCategoryFieldSpec extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_category_field_spec_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private IndustryDataCategory industryDataCategory;

    // null이면 해당 카테고리의 모든 연도에 공통 적용. 특정 연도만 다르게 가져가고 싶을 때만 채움.
    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;

    private String fieldKey;

    private String fieldLabel;

    private Integer displayOrder;

    public static IndustryCategoryFieldSpec create(
            IndustryDataCategory category,
            Year year,
            String fieldKey,
            String fieldLabel,
            int displayOrder
    ) {
        IndustryCategoryFieldSpec spec = new IndustryCategoryFieldSpec();
        spec.industryDataCategory = category;
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
