package kr.co.hdi.admin.user.service;

import kr.co.hdi.admin.user.dto.request.ExpertInfoRequest;
import kr.co.hdi.admin.user.dto.request.ExpertInfoUpdateRequest;
import kr.co.hdi.admin.user.dto.response.ExpertInfoResponse;
import kr.co.hdi.admin.user.dto.response.ExpertNameResponse;
import kr.co.hdi.domain.user.entity.Role;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.user.exception.AuthErrorCode;
import kr.co.hdi.domain.user.exception.AuthException;
import kr.co.hdi.domain.user.repository.UserRepository;
import kr.co.hdi.domain.year.entity.UserYearRound;
import kr.co.hdi.domain.year.repository.UserYearRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpertUserService {

    private final UserRepository userRepository;
    private final UserYearRoundRepository userYearRoundRepository;

    /*
    특정 분야의 등록된 전문가 전체 조회
     */
    public List<ExpertInfoResponse> getExpertInfo(UserType type) {

        List<UserEntity> users = userRepository.findExpertByType(type, Role.USER);
        return attachRounds(type, users);
    }

    /*
    전문가 페이지 검색
     */
    public List<ExpertInfoResponse> searchExpert(UserType type, String q) {

        List<UserEntity> users = userRepository.searchExperts(type, q);
        return attachRounds(type, users);
    }

    private List<ExpertInfoResponse> attachRounds(UserType type, List<UserEntity> users) {

        List<UserYearRound> userYearRounds =
                userYearRoundRepository.findAllByUserType(type);

        Map<UserEntity, List<UserYearRound>> grouped =
                userYearRounds.stream()
                        .collect(Collectors.groupingBy(UserYearRound::getUser));

        return users.stream()
                .map(user -> {
                    List<UserYearRound> rounds = grouped.getOrDefault(user, List.of());
                    return ExpertInfoResponse.from(user, toRoundStrings(rounds));
                })
                .toList();
    }

    private List<String> toRoundStrings(List<UserYearRound> rounds) {
        return rounds.stream()
                .map(this::toRoundString)
                .toList();
    }

    private String toRoundString(UserYearRound uyr) {
        var ar = uyr.getAssessmentRound();
        return ar.getYear().getYear() + "년 " + ar.getAssessmentRound() + "차수";
    }

    /*
    새로운 전문가 등록
     */
    @Transactional
    public void registerExpert(
            UserType type, ExpertInfoRequest request
    ) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_EXISTS);
        }

        UserEntity newUser = UserEntity.createExpert(request, type, request.password());
        userRepository.save(newUser);
    }

    /*
    전문가 정보 수정
     */
    @Transactional
    public void updateExpertInfo(ExpertInfoUpdateRequest request, Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        user.updateInfo(request);
        userRepository.save(user);
    }
}
