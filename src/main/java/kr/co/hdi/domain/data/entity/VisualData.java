package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.admin.data.dto.request.VisualDataRequest;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.data.enums.VisualImageType;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class VisualData extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_data_id")
    private Long id;

    private String brandCode;

    private String brandName;

    private String sectorCategory;

    @Column(columnDefinition = "text")
    private String referenceUrl;

    @Enumerated(EnumType.STRING)
    private VisualDataCategory visualDataCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;

    private Integer brandCodeInteger;

    /*
    IndustryData와 동일한 패턴. 카테고리마다 다른 스펙(mainProductCategory, title,
    designDescription 등)을 고정 컬럼 대신 attributes(jsonb)로 뺐다.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> attributes = new HashMap<>();

    // 로고 이미지 슬롯. IndustryData.images와 동일한 패턴.
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "visualData", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VisualDataImage> images = new ArrayList<>();

    public void delete() {
        processDeletion();
    }

    private VisualDataImage image(VisualImageType type) {
        return images.stream()
                .filter(img -> img.getImageType() == type)
                .findFirst()
                .orElse(null);
    }

    public String getLogoImage() {
        VisualDataImage img = image(VisualImageType.LOGO);
        return img == null ? null : img.getS3Key();
    }

    public String getOriginalLogoImage() {
        VisualDataImage img = image(VisualImageType.LOGO);
        return img == null ? null : img.getOriginalFileName();
    }

    public void deleteImage() {
        VisualDataImage img = image(VisualImageType.LOGO);
        if (img != null) {
            img.updateOriginalFileName(null);
        }
    }

    /*
    VisualData 생성
    Year는 Service 계층에서 주입된다.
     */
    public static VisualData create(Year year, VisualDataRequest request) {
        VisualData v = new VisualData();

        v.year = year;
        v.brandCode = request.code();
        v.brandName = request.name();
        v.sectorCategory = request.sectorCategory();
        v.referenceUrl = request.referenceUrl();
        v.visualDataCategory = request.visualDataCategory();

        v.attributes = request.attributes() == null ? new HashMap<>() : new HashMap<>(request.attributes());

        v.images.add(VisualDataImage.create(v, VisualImageType.LOGO, "2026/VI/" + UUID.randomUUID(), request.originalLogoImage()));

        return v;
    }

    /*
    VisualData 복제
    id, createdAt, updatedAt, deletedAt 제외
     */
    public VisualData duplicate() {

        VisualData copy = new VisualData();

        copy.brandCode = this.brandCode;
        copy.brandName = this.brandName;
        copy.sectorCategory = this.sectorCategory;
        copy.referenceUrl = this.referenceUrl;

        copy.visualDataCategory = this.visualDataCategory;
        copy.year = this.year;
        copy.attributes = new HashMap<>(this.attributes);

        copy.images.add(VisualDataImage.create(copy, VisualImageType.LOGO, "2026/VI/" + UUID.randomUUID(), null));

        return copy;
    }

    /*
    VisualData 부분 수정
     */
    public void updatePartial(VisualDataRequest request) {

        if (request.code() != null) {
            this.brandCode = request.code();
        }
        if (request.name() != null) {
            this.brandName = request.name();
        }
        if (request.sectorCategory() != null) {
            this.sectorCategory = request.sectorCategory();
        }
        if (request.referenceUrl() != null) {
            this.referenceUrl = request.referenceUrl();
        }

        if (request.visualDataCategory() != null) {
            this.visualDataCategory = request.visualDataCategory();
        }
        if (request.originalLogoImage() != null) {
            VisualDataImage img = image(VisualImageType.LOGO);
            if (img == null) {
                images.add(VisualDataImage.create(this, VisualImageType.LOGO, "2026/VI/" + UUID.randomUUID(), request.originalLogoImage()));
            } else {
                img.updateOriginalFileName(request.originalLogoImage());
            }
        }
        if (request.attributes() != null) {
            if (this.attributes == null) {
                this.attributes = new HashMap<>();
            }
            this.attributes.putAll(request.attributes());
        }
    }

    @PrePersist
    @PreUpdate
    private void syncBrandCodeInteger() {
        if (this.brandCode != null) {
            try {
                this.brandCodeInteger = Integer.parseInt(this.brandCode);
            } catch (NumberFormatException e) {
                this.brandCodeInteger = null;
            }
        }
    }



}