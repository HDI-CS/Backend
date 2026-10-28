package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.admin.data.dto.request.IndustryDataRequest;
import kr.co.hdi.admin.data.dto.request.VisualDataRequest;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;

import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.enums.IndustryImageType;
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
public class IndustryData extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
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

    private String usage;
    private String shoppingUrl;

    @Column(name = "original_id")
    private String originalId;

    @Enumerated(EnumType.STRING)
    private IndustryDataCategory industryDataCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Year year;

    private Integer originalIdInteger;

    /*
    상세/정면/측면/측면2/측면3 이미지 슬롯. 예전엔 슬롯당 2컬럼(S3 키 + 원본 파일명)씩
    고정 컬럼으로 뒀는데, 슬롯이 늘어날 때마다(서브이미지04 등) 컬럼을 추가해야 하는 구조라
    행 단위(IndustryDataImage)로 뺐다. 아래 getXxxImagePath()/getOriginalXxxImagePath()는
    호출부(IndustryDataService, DTO 등)를 안 건드리려고 예전 getter 이름/시그니처를 그대로 유지.
     */
    // 리스트 조회 시 row마다 images를 따로 SELECT하는 N+1을 막기 위해 IN 절로 묶어서 가져오게 함
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "industryData", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IndustryDataImage> images = new ArrayList<>();

    /*
    카테고리마다 스펙 필드가 다르고 매 회차 늘어나는 걸 고정 컬럼으로 못 따라가서(noiseCancelling,
    waterproof 등 "// 2026" 블록 참고) 여기로 몰아둔다. 모든 카테고리가 공통으로 쓰는 필드
    (companyName, productName, price, weight...)는 그대로 고정 컬럼 유지, 카테고리별로 들쑥날쑥한
    것만 여기 key-value로 넣는다. 카테고리별 필드 라벨은 Frontend의 카테고리 스펙 설정에서 관리.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> attributes = new HashMap<>();

    public void delete() {
        processDeletion();
    }

    private IndustryDataImage image(IndustryImageType type) {
        return images.stream()
                .filter(img -> img.getImageType() == type)
                .findFirst()
                .orElse(null);
    }

    public String getDetailImagePath() {
        IndustryDataImage img = image(IndustryImageType.DETAIL);
        return img == null ? null : img.getS3Key();
    }

    public String getOriginalDetailImagePath() {
        IndustryDataImage img = image(IndustryImageType.DETAIL);
        return img == null ? null : img.getOriginalFileName();
    }

    public String getFrontImagePath() {
        IndustryDataImage img = image(IndustryImageType.FRONT);
        return img == null ? null : img.getS3Key();
    }

    public String getOriginalFrontImagePath() {
        IndustryDataImage img = image(IndustryImageType.FRONT);
        return img == null ? null : img.getOriginalFileName();
    }

    public String getSideImagePath() {
        IndustryDataImage img = image(IndustryImageType.SIDE);
        return img == null ? null : img.getS3Key();
    }

    public String getOriginalSideImagePath() {
        IndustryDataImage img = image(IndustryImageType.SIDE);
        return img == null ? null : img.getOriginalFileName();
    }

    public String getSide2ImagePath() {
        IndustryDataImage img = image(IndustryImageType.SIDE2);
        return img == null ? null : img.getS3Key();
    }

    public String getOriginalSide2ImagePath() {
        IndustryDataImage img = image(IndustryImageType.SIDE2);
        return img == null ? null : img.getOriginalFileName();
    }

    public String getSide3ImagePath() {
        IndustryDataImage img = image(IndustryImageType.SIDE3);
        return img == null ? null : img.getS3Key();
    }

    public String getOriginalSide3ImagePath() {
        IndustryDataImage img = image(IndustryImageType.SIDE3);
        return img == null ? null : img.getOriginalFileName();
    }

    /*
    deleteImage(): 예전엔 originalXxxImagePath만 null로 만들고 s3Key는 그대로 둬서
    (다음 업로드가 같은 키를 재사용) S3 키를 반환해 호출부가 실제 S3 객체도 지우게 했다.
    행 기반으로 바뀌어도 동작은 동일하게 유지.
     */
    public String deleteImage(String imageStatus) {
        IndustryImageType type;
        try {
            type = IndustryImageType.valueOf(imageStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }

        IndustryDataImage img = image(type);
        if (img == null) {
            return null;
        }
        img.updateOriginalFileName(null);
        return img.getS3Key();
    }

    /*
    IndustryData 생성
     */
    public static IndustryData create(Year year, IndustryDataRequest request) {
        IndustryData i = new IndustryData();

        i.year = year;
        i.originalId = request.code();
        i.companyName = request.companyName();
        i.productName = request.productName();
        i.modelName = request.modelName();
        i.price = request.price();
        i.material = request.material();
        i.size = request.size();
        i.weight = request.weight();
        i.referenceUrl = request.referenceUrl();
        i.registeredAt = request.registeredAt();
        i.productPath = request.productPath();
        i.productTypeName = request.productTypeName();
        i.industryDataCategory = request.industryDataCategory();

        i.usage = request.usage();
        i.shoppingUrl = request.shoppingUrl();

        i.attributes = request.attributes() == null ? new HashMap<>() : new HashMap<>(request.attributes());

        i.images.add(IndustryDataImage.create(i, IndustryImageType.DETAIL, "2026/ID/" + UUID.randomUUID(), request.originalDetailImagePath()));
        i.images.add(IndustryDataImage.create(i, IndustryImageType.FRONT, "2026/ID/" + UUID.randomUUID(), request.originalFrontImagePath()));
        i.images.add(IndustryDataImage.create(i, IndustryImageType.SIDE, "2026/ID/" + UUID.randomUUID(), request.originalSideImagePath()));
        i.images.add(IndustryDataImage.create(i, IndustryImageType.SIDE2, "2026/ID/" + UUID.randomUUID(), request.originalSide2ImagePath()));
        i.images.add(IndustryDataImage.create(i, IndustryImageType.SIDE3, "2026/ID/" + UUID.randomUUID(), request.originalSide3ImagePath()));

        return i;
    }

    /*
    IndustryData 복제
    id, createdAt, updatedAt, deletedAt 제외
     */

    public IndustryData duplicate() {

        IndustryData copy = new IndustryData();

        copy.year = this.year;
        copy.originalId = this.originalId;
        copy.companyName = this.companyName;
        copy.productName = this.productName;
        copy.modelName = this.modelName;
        copy.price = this.price;
        copy.material = this.material;
        copy.size = this.size;
        copy.weight = this.weight;
        copy.referenceUrl = this.referenceUrl;
        copy.registeredAt = this.registeredAt;
        copy.productPath = this.productPath;
        copy.productTypeName = this.productTypeName;
        copy.industryDataCategory = this.industryDataCategory;

        copy.usage = this.usage;
        copy.shoppingUrl = this.shoppingUrl;

        copy.attributes = new HashMap<>(this.attributes);

        for (IndustryImageType type : IndustryImageType.values()) {
            copy.images.add(IndustryDataImage.create(copy, type, "2026/ID/" + UUID.randomUUID(), null));
        }

        return copy;
    }

    /*
    IndustryData 부분 수정
     */
    public void updatePartial(IndustryDataRequest request) {

        if (request.code() != null) {
            this.originalId = request.code();
        }
        if (request.companyName() != null) {
            this.companyName = request.companyName();
        }
        if (request.productName() != null) {
            this.productName = request.productName();
        }
        if (request.modelName() != null) {
            this.modelName = request.modelName();
        }
        if (request.price() != null) {
            this.price = request.price();
        }
        if (request.material() != null) {
            this.material = request.material();
        }
        if (request.size() != null) {
            this.size = request.size();
        }
        if (request.weight() != null) {
            this.weight = request.weight();
        }
        if (request.referenceUrl() != null) {
            this.referenceUrl = request.referenceUrl();
        }
        if (request.registeredAt() != null) {
            this.registeredAt = request.registeredAt();
        }
        if (request.productPath() != null) {
            this.productPath = request.productPath();
        }
        if (request.productTypeName() != null) {
            this.productTypeName = request.productTypeName();
        }
        if (request.industryDataCategory() != null) {
            this.industryDataCategory = request.industryDataCategory();
        }
        updateOriginalFileNameIfPresent(IndustryImageType.DETAIL, request.originalDetailImagePath());
        updateOriginalFileNameIfPresent(IndustryImageType.FRONT, request.originalFrontImagePath());
        updateOriginalFileNameIfPresent(IndustryImageType.SIDE, request.originalSideImagePath());
        updateOriginalFileNameIfPresent(IndustryImageType.SIDE2, request.originalSide2ImagePath());
        updateOriginalFileNameIfPresent(IndustryImageType.SIDE3, request.originalSide3ImagePath());

        if (request.usage() != null) {
            this.usage = request.usage();
        }
        if (request.shoppingUrl() != null) {
            this.shoppingUrl = request.shoppingUrl();
        }
        if (request.attributes() != null) {
            if (this.attributes == null) {
                this.attributes = new HashMap<>();
            }
            this.attributes.putAll(request.attributes());
        }
    }


    private void updateOriginalFileNameIfPresent(IndustryImageType type, String originalFileName) {
        if (originalFileName == null) {
            return;
        }
        IndustryDataImage img = image(type);
        if (img == null) {
            // 구 데이터는 side2/side3 슬롯이 아예 없을 수 있어 새로 업로드하면 그때 키를 만든다.
            images.add(IndustryDataImage.create(this, type, "2026/ID/" + UUID.randomUUID(), originalFileName));
        } else {
            img.updateOriginalFileName(originalFileName);
        }
    }

    @PrePersist
    @PreUpdate
    private void syncOriginalIdInteger() {
        if (this.originalId != null) {
            try {
                this.originalIdInteger = Integer.parseInt(this.originalId);
            } catch (NumberFormatException e) {
                this.originalIdInteger = null;
            }
        }
    }

}