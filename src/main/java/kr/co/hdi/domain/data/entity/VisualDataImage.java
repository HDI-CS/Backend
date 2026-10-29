package kr.co.hdi.domain.data.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.VisualImageType;
import kr.co.hdi.global.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

/*
VisualData의 이미지 슬롯(현재는 로고 하나)을 고정 컬럼 대신 행으로 관리.
IndustryDataImage와 동일한 패턴 — 슬롯이 늘어나도(예: 서브이미지) 컬럼 추가 없이
VisualImageType에 값만 추가하면 됨.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "visual_data_image",
        uniqueConstraints = @UniqueConstraint(columnNames = {"visual_data_id", "image_type"})
)
public class VisualDataImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "visual_data_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visual_data_id", nullable = false)
    private VisualData visualData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisualImageType imageType;

    @Column(name = "s3_key", columnDefinition = "text")
    private String s3Key;

    @Column(columnDefinition = "text")
    private String originalFileName;

    static VisualDataImage create(VisualData visualData, VisualImageType imageType, String s3Key, String originalFileName) {
        VisualDataImage image = new VisualDataImage();
        image.visualData = visualData;
        image.imageType = imageType;
        image.s3Key = s3Key;
        image.originalFileName = originalFileName;
        return image;
    }

    void updateOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
