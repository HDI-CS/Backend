package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.IndustryImageType;
import kr.co.hdi.global.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

/*
IndustryData의 이미지 슬롯(상세/정면/측면/측면2/측면3)을 고정 컬럼 대신 행으로 관리.
슬롯이 늘어나도(서브이미지04 등) 컬럼 추가 없이 IndustryImageType에 값만 추가하면 됨.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndustryDataImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "industry_data_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_data_id", nullable = false)
    private IndustryData industryData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndustryImageType imageType;

    @Column(columnDefinition = "text")
    private String s3Key;

    @Column(columnDefinition = "text")
    private String originalFileName;

    static IndustryDataImage create(IndustryData industryData, IndustryImageType imageType, String s3Key, String originalFileName) {
        IndustryDataImage image = new IndustryDataImage();
        image.industryData = industryData;
        image.imageType = imageType;
        image.s3Key = s3Key;
        image.originalFileName = originalFileName;
        return image;
    }

    void updateOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
