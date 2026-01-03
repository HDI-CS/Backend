package kr.co.hdi.global.scheduler;

import kr.co.hdi.domain.currentSurvey.entity.CurrentSurvey;
import kr.co.hdi.domain.currentSurvey.repository.CurrentSurveyRepository;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CurrentSurveyScheduler {

    private final AssessmentRoundRepository assessmentRoundRepository;
    private final CurrentSurveyRepository currentSurveyRepository;

    @Transactional
    @Scheduled(cron = "0 15 0 * * *", zone = "Asia/Seoul")
    public void syncCurrentSurvey() {
        LocalDate today = LocalDate.now();

        for (DomainType domainType : DomainType.values()) {

            CurrentSurvey cs = currentSurveyRepository.findByDomainType(domainType)
                    .orElseGet(() ->
                            currentSurveyRepository.save(
                                    CurrentSurvey.create(domainType, null, null)
                            )
                    );

            assessmentRoundRepository.findCurrentRound(domainType, today)
                    .ifPresent(round -> {
                        Long yearId = round.getYear().getId();
                        Long roundId = round.getId();

                        if (!Objects.equals(cs.getYearId(), yearId)
                                || !Objects.equals(cs.getAssessmentRoundId(), roundId)) {
                            cs.update(yearId, roundId);
                        }
                    });
        }
    }
}
