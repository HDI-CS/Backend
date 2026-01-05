package kr.co.hdi.domain.user.service;

import jakarta.transaction.Transactional;
import kr.co.hdi.admin.assignment.service.IndustryAssignmentService;
import kr.co.hdi.admin.survey.exception.SurveyErrorCode;
import kr.co.hdi.admin.survey.exception.SurveyException;
import kr.co.hdi.domain.assignment.entity.IndustryDataAssignment;
import kr.co.hdi.domain.assignment.entity.VisualDataAssignment;
import kr.co.hdi.domain.assignment.repository.IndustryDataAssignmentRepository;
import kr.co.hdi.domain.assignment.repository.VisualDataAssignmentRepository;
import kr.co.hdi.domain.currentSurvey.entity.CurrentSurvey;
import kr.co.hdi.domain.currentSurvey.repository.CurrentSurveyRepository;
import kr.co.hdi.domain.user.entity.UserEntity;
import kr.co.hdi.domain.user.dto.response.AuthResponse;
import kr.co.hdi.domain.user.entity.UserType;
import kr.co.hdi.domain.user.exception.AuthException;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.survey.service.SurveyService;
import kr.co.hdi.domain.user.exception.AuthErrorCode;
import kr.co.hdi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final SurveyService surveyService;
    private final IndustryAssignmentService industryAssignmentService;
    private final CurrentSurveyRepository currentSurveyRepository;
    private final IndustryDataAssignmentRepository industryDataAssignmentRepository;
    private final VisualDataAssignmentRepository visualDataAssignmentRepository;

    /*
    로그인
     */
    public AuthResponse login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

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
        DomainType type = user.getUserType().toDomainType();

        CurrentSurvey currentSurvey = currentSurveyRepository.findByDomainType(type)
                .orElseThrow(() -> new SurveyException(SurveyErrorCode.INVALID_DOMAIN_TYPE));

        Boolean surveyDone = Boolean.FALSE;

        if (type == DomainType.INDUSTRY) {
            List<IndustryDataAssignment> assignments =
                    industryDataAssignmentRepository.findAssignmentsByUserAndAssessmentRound(
                            userId, currentSurvey.getAssessmentRoundId());

            surveyDone = assignments.stream()
                    .allMatch(a -> a.getSurveyCount() != null
                            && a.getResponseCount() != null
                            && a.getSurveyCount().equals(a.getResponseCount()));

        } else if (type == DomainType.VISUAL) {
            List<VisualDataAssignment> assignments =
                    visualDataAssignmentRepository.findAssignmentsByUserAndAssessmentRound(
                            userId, currentSurvey.getAssessmentRoundId());

            surveyDone = assignments.stream()
                    .allMatch(a -> a.getSurveyCount() != null
                            && a.getResponseCount() != null
                            && a.getSurveyCount().equals(a.getResponseCount()));

        }

        return AuthResponse.from(user, surveyDone);
    }
}