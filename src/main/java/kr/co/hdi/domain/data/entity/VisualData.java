package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.admin.data.dto.request.VisualDataRequest;
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

    @Column(columnDefinition = "text")
    private String logoImage;

    public void delete() {
        processDeletion();
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
        v.mainProductCategory = request.mainProductCategory();
        v.mainProduct = request.mainProduct();
        v.target = request.target();
        v.referenceUrl = request.referenceUrl();
        v.visualDataCategory = request.visualDataCategory();

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
        copy.mainProductCategory = this.mainProductCategory;
        copy.mainProduct = this.mainProduct;
        copy.target = this.target;
        copy.referenceUrl = this.referenceUrl;
        copy.visualDataCategory = this.visualDataCategory;
        copy.year = this.year;

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
        if (request.mainProductCategory() != null) {
            this.mainProductCategory = request.mainProductCategory();
        }
        if (request.mainProduct() != null) {
            this.mainProduct = request.mainProduct();
        }
        if (request.target() != null) {
            this.target = request.target();
        }
        if (request.referenceUrl() != null) {
            this.referenceUrl = request.referenceUrl();
        }
        if (request.visualDataCategory() != null) {
            this.visualDataCategory = request.visualDataCategory();
        }
    }
}
