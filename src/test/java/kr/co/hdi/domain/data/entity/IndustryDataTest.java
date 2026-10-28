package kr.co.hdi.domain.data.entity;

import kr.co.hdi.admin.data.dto.request.IndustryDataRequest;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class IndustryDataTest {

    private IndustryDataRequest requestWithImages(
            String originalDetail, String originalFront, String originalSide,
            String originalSide2, String originalSide3
    ) {
        return new IndustryDataRequest(
                "CODE-1", "제품명", "회사명", "10000", "https://example.com",
                "2026-01-01", "경로", "타입", "1kg",
                "용도", "https://shop.example.com",
                Map.of(),
                originalDetail, originalFront, originalSide, originalSide2, originalSide3,
                IndustryDataCategory.HEADPHONE
        );
    }

    @Test
    void create_populatesAllFiveImageSlotsFromRequest() {
        IndustryData data = IndustryData.create(null, requestWithImages(
                "detail.jpg", "front.jpg", "side.jpg", "side2.jpg", "side3.jpg"
        ));

        assertThat(data.getOriginalDetailImagePath()).isEqualTo("detail.jpg");
        assertThat(data.getOriginalFrontImagePath()).isEqualTo("front.jpg");
        assertThat(data.getOriginalSideImagePath()).isEqualTo("side.jpg");
        assertThat(data.getOriginalSide2ImagePath()).isEqualTo("side2.jpg");
        assertThat(data.getOriginalSide3ImagePath()).isEqualTo("side3.jpg");

        // s3 key는 매 슬롯 랜덤 UUID로 서로 달라야 함
        assertThat(data.getDetailImagePath()).isNotBlank();
        assertThat(data.getDetailImagePath()).isNotEqualTo(data.getFrontImagePath());
    }

    @Test
    void create_withNullOriginalFileName_leavesSlotPresentButUnresolved() {
        IndustryData data = IndustryData.create(null, requestWithImages(
                "detail.jpg", null, null, null, null
        ));

        // s3 key는 슬롯이 항상 만들어지므로 null이 아니지만, 원본 파일명이 없으면
        // IndustryDataService.resolveIndustryImageUrl()이 "이미지 없음"으로 취급한다.
        assertThat(data.getFrontImagePath()).isNotBlank();
        assertThat(data.getOriginalFrontImagePath()).isNull();
    }

    @Test
    void duplicate_assignsNewKeysAndDropsOriginalFileNames() {
        IndustryData original = IndustryData.create(null, requestWithImages(
                "detail.jpg", "front.jpg", "side.jpg", "side2.jpg", "side3.jpg"
        ));

        IndustryData copy = original.duplicate();

        assertThat(copy.getDetailImagePath()).isNotEqualTo(original.getDetailImagePath());
        assertThat(copy.getOriginalDetailImagePath()).isNull();
        assertThat(copy.getOriginalFrontImagePath()).isNull();
    }

    @Test
    void deleteImage_clearsOriginalFileNameButKeepsS3Key() {
        IndustryData data = IndustryData.create(null, requestWithImages(
                "detail.jpg", null, null, null, null
        ));
        String keyBeforeDelete = data.getDetailImagePath();

        String returnedKey = data.deleteImage("DETAIL");

        assertThat(returnedKey).isEqualTo(keyBeforeDelete);
        assertThat(data.getDetailImagePath()).isEqualTo(keyBeforeDelete);
        assertThat(data.getOriginalDetailImagePath()).isNull();
    }

    @Test
    void deleteImage_withUnknownStatus_returnsNullAndChangesNothing() {
        IndustryData data = IndustryData.create(null, requestWithImages(
                "detail.jpg", null, null, null, null
        ));

        String result = data.deleteImage("NOT_A_REAL_TYPE");

        assertThat(result).isNull();
        assertThat(data.getOriginalDetailImagePath()).isEqualTo("detail.jpg");
    }

    @Test
    void updatePartial_updatesOnlyProvidedOriginalFileNames() {
        IndustryData data = IndustryData.create(null, requestWithImages(
                "detail.jpg", "front.jpg", null, null, null
        ));

        IndustryDataRequest patch = requestWithImages(null, "front-v2.jpg", null, null, null);
        data.updatePartial(patch);

        // null로 넘어온 필드는 기존 값 유지
        assertThat(data.getOriginalDetailImagePath()).isEqualTo("detail.jpg");
        // 값이 있는 필드만 갱신
        assertThat(data.getOriginalFrontImagePath()).isEqualTo("front-v2.jpg");
    }

    @Test
    void updatePartial_mergesAttributesInsteadOfReplacing() {
        IndustryData data = IndustryData.create(null, new IndustryDataRequest(
                "CODE-1", "제품명", "회사명", "10000", "https://example.com",
                "2026-01-01", "경로", "타입", "1kg",
                "용도", "https://shop.example.com",
                Map.of("codec", "AAC"),
                null, null, null, null, null,
                IndustryDataCategory.HEADPHONE
        ));

        IndustryDataRequest patch = requestWithImages(null, null, null, null, null);
        IndustryDataRequest patchWithAttributes = new IndustryDataRequest(
                patch.code(), patch.productName(), patch.companyName(), patch.price(), patch.referenceUrl(),
                patch.registeredAt(), patch.productPath(), patch.productTypeName(), patch.weight(),
                patch.usage(), patch.shoppingUrl(),
                Map.of("chargeTime", "2시간"),
                patch.originalDetailImagePath(), patch.originalFrontImagePath(), patch.originalSideImagePath(),
                patch.originalSide2ImagePath(), patch.originalSide3ImagePath(), patch.industryDataCategory()
        );

        data.updatePartial(patchWithAttributes);

        assertThat(data.getAttributes())
                .containsEntry("codec", "AAC")
                .containsEntry("chargeTime", "2시간");
    }
}
