package kr.co.hdi.global.scheduler;

import kr.co.hdi.domain.currentSurvey.entity.CurrentSurvey;
import kr.co.hdi.domain.currentSurvey.repository.CurrentSurveyRepository;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CurrentSurveyScheduler {

    private final AssessmentRoundRepository assessmentRoundRepository;
    private final CurrentSurveyRepository currentSurveyRepository;

    @Transactional
    @Scheduled(cron = "0 15 0 * * *", zone = "Asia/Seoul")
    public void syncCurrentSurvey() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        for (DomainType domainType : DomainType.values()) {

            CurrentSurvey cs = currentSurveyRepository.findByDomainType(domainType)
                    .orElseGet(() ->
                            currentSurveyRepository.save(
                                    CurrentSurvey.create(domainType, null, null)
                            )
                    );

            // 기간이 겹치는 회차가 여럿이면 더 나중에 생성된(createdAt 최신) 회차를 우선한다.
            List<AssessmentRound> currentRounds = assessmentRoundRepository.findCurrentRounds(domainType, today);

            currentRounds.stream().findFirst()
                    .ifPresentOrElse(round -> {
                        Long yearId = round.getYear().getId();
                        Long roundId = round.getId();

                        if (!Objects.equals(cs.getYearId(), yearId)
                                || !Objects.equals(cs.getAssessmentRoundId(), roundId)
                                || !cs.isSurveyStatus()) {
                            cs.update(yearId, roundId, true);
                        }
                    }, () -> {
                        if (cs.isSurveyStatus()) {
                            cs.update(cs.getYearId(), cs.getAssessmentRoundId(), false);
                        }
                    });
        }
    }
}
