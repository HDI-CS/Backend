package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class VisualData extends BaseTimeEntityWithDeletion {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_data_id")
    private Long id;

    private String brandCode;

    private String brandName;

    private String sectorCategory;

    private String mainProductCategory;

    private String mainProduct;

    private String target;

    @Column(columnDefinition = "text")
    private String referenceUrl;

    @Enumerated(EnumType.STRING)
    private VisualDataCategory visualDataCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;
}
