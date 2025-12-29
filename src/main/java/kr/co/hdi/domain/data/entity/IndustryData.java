package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.admin.data.dto.request.IndustryDataRequest;
import kr.co.hdi.admin.data.dto.request.VisualDataRequest;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

    @Column(columnDefinition = "text")
    private String detailImagePath;   // 상세 이미지 S3 Key

    @Column(columnDefinition = "text")
    private String originalDetailImagePath;

    @Column(columnDefinition = "text")
    private String frontImagePath;    // 정면 이미지 S3 Key

    @Column(columnDefinition = "text")
    private String originalFrontImagePath;

    @Column(columnDefinition = "text")
    private String sideImagePath;     // 측면 이미지 S3 Key

    @Column(columnDefinition = "text")
    private String originalSideImagePath;

    public void delete() {
        processDeletion();
    }

    public String deleteImage(String imageStatus) {

        if (imageStatus.equals("DETAIL")) {
            deleteDetailImage();
            return this.detailImagePath;
        }
        if (imageStatus.equals("FRONT")) {
            deleteFrontImage();
            return this.frontImagePath;
        }
        if (imageStatus.equals("SIDE")) {
            deleteSideImage();
            return this.sideImagePath;
        }
        return null;
    }

    private void deleteDetailImage() {
        this.originalDetailImagePath = null;
    }

    private void deleteFrontImage() {
        this.originalFrontImagePath = null;
    }

    private void deleteSideImage() {
        this.originalSideImagePath = null;
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

        i.originalDetailImagePath = request.originalDetailImagePath();
        i.detailImagePath = "2026/ID/" + UUID.randomUUID();

        i.originalFrontImagePath = request.originalFrontImagePath();
        i.frontImagePath = "2026/ID/" + UUID.randomUUID();

        i.originalSideImagePath = request.originalSideImagePath();
        i.sideImagePath = "2026/ID/" + UUID.randomUUID();

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

        copy.detailImagePath = "2026/ID/" + UUID.randomUUID();
        copy.frontImagePath = "2026/ID/" + UUID.randomUUID();
        copy.sideImagePath = "2026/ID/" + UUID.randomUUID();

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
            this.price= request.price();
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
        if (request.originalDetailImagePath() != null) {
            this.originalDetailImagePath = request.originalDetailImagePath();
        }
        if (request.originalFrontImagePath() != null) {
            this.originalFrontImagePath = request.originalFrontImagePath();
        }
        if (request.originalSideImagePath() != null) {
            this.originalSideImagePath = request.originalSideImagePath();
        }
    }
}
