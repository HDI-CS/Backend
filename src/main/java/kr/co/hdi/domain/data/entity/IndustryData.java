package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class IndustryData extends BaseTimeEntityWithDeletion {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_data_id")
    private Long id;

    private String companyName;

    @Column(nullable = false)
    private String productName;

    private String modelName;
    private String price;
    private String material;
    private String size;
    private String weight;
    private String referenceUrl;

    private String registeredAt;
    private String productPath;

    private String productTypeName;

    @Column(name = "original_id")
    private String originalId;

    @Enumerated(EnumType.STRING)
    private IndustryDataCategory industryDataCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;
}
