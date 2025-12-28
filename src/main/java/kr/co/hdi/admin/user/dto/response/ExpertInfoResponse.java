package kr.co.hdi.admin.user.dto.response;

import kr.co.hdi.domain.user.entity.UserEntity;

import java.util.List;

public record ExpertInfoResponse(
        Long memberId,
        String name,
        List<String > rounds,
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

    public static ExpertInfoResponse from(UserEntity user, List<String> round) {

        return new ExpertInfoResponse(
                user.getId(),
                user.getName(),
                round,
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getAge(),
                user.getCareer(),
                user.getAcademic(),
                user.getExpertise(),
                user.getCompany(),
                user.getNote()
        );
    }
}
