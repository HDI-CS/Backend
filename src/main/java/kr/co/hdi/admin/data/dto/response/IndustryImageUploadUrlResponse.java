package kr.co.hdi.admin.data.dto.response;

public record IndustryImageUploadUrlResponse(
        String detailUploadUrl,
        String frontUploadUrl,
        String sideUploadUrl,
        String side2UploadUrl,
        String side3UploadUrl
) {
}
