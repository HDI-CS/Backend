package kr.co.hdi.admin.user.dto.request;

public record ExpertInfoRequest(
        String name,
        String email,
        String password,
        String phoneNumber,
        String gender,
        String age,
        String career,
        String academic,
        String expertise,
        String company,
        String note
) {
}
