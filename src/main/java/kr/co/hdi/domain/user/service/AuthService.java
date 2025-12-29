package kr.co.hdi.domain.user.service;

import jakarta.transaction.Transactional;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.dto.response.AuthResponse;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.user.exception.AuthException;
import kr.co.hdi.survey.service.SurveyService;
import kr.co.hdi.domain.user.exception.AuthErrorCode;
import kr.co.hdi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final SurveyService surveyService;

    /*
    로그인
     */
    public AuthResponse login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new AuthException(AuthErrorCode.INVALID_PASSWORD, "잘못된 비밀번호입니다.");
//        }
        if (!user.getPassword().equals(password)) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD, "잘못된 비밀번호입니다.");
        }


        return AuthResponse.from(user);
    }

    /*
    전문가 계정 생성
     */
    public AuthResponse createUser(String email, String password, String name, UserType type) {
        if (userRepository.existsByEmail(email)) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다.");
        }

        UserEntity user = UserEntity.createUser(email, password, name, type);

        userRepository.save(user);
        return AuthResponse.from(user);
    }

    /*
    어드민 계정 생성
     */
    public AuthResponse createAdmin(String email, String password, String name, UserType type) {
        if (userRepository.existsByEmail(email)) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다.");
        }

        UserEntity user = UserEntity.createAdmin(email, password, name, type);

        userRepository.save(user);
        return AuthResponse.from(user);
    }

    /*
    특정 회원의 정보 조회
     */
    public AuthResponse getAuthInfo(Long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        // 평가 완료 확인
        surveyService.checkSurveyDone(userId);

        return AuthResponse.from(user);
    }
}