package kr.co.hdi.global.scheduler;

import kr.co.hdi.domain.currentSurvey.entity.CurrentSurvey;
import kr.co.hdi.domain.currentSurvey.repository.CurrentSurveyRepository;
import kr.co.hdi.domain.year.entity.AssessmentRound;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.domain.year.repository.AssessmentRoundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentSurveySchedulerTest {

    @Mock
    private AssessmentRoundRepository assessmentRoundRepository;

    @Mock
    private CurrentSurveyRepository currentSurveyRepository;

    @InjectMocks
    private CurrentSurveyScheduler scheduler;

    @Test
    void syncCurrentSurvey_whenRoundsOverlap_picksFirstRoundInRepositoryOrder() {
        // 회차 기간이 겹치면 findCurrentRounds가 createdAt desc로 정렬해서 돌려주고,
        // 스케줄러는 그중 첫 번째(=더 나중에 생성된 회차)를 그대로 신뢰해서 선택해야 한다.
        CurrentSurvey industryCurrent = CurrentSurvey.create(DomainType.INDUSTRY, 14L, 18L);
        when(currentSurveyRepository.findByDomainType(DomainType.INDUSTRY))
                .thenReturn(Optional.of(industryCurrent));
        when(currentSurveyRepository.findByDomainType(DomainType.VISUAL))
                .thenReturn(Optional.of(CurrentSurvey.create(DomainType.VISUAL, 1L, 1L)));

        Year year = mock(Year.class);
        when(year.getId()).thenReturn(14L);

        AssessmentRound newerRound = mock(AssessmentRound.class); // 더 나중에 생성됨 (예: id=19)
        when(newerRound.getId()).thenReturn(19L);
        when(newerRound.getYear()).thenReturn(year);

        // 기간이 겹치는 이전 회차 (예: id=18). 스케줄러가 목록의 첫 번째만 사용하는지 검증하기 위한
        // 자리로만 필요하고, 실제로 getId()/getYear()가 호출되면 안 된다 (호출되면 첫 번째를 안 쓴다는 뜻).
        AssessmentRound olderRound = mock(AssessmentRound.class);

        when(assessmentRoundRepository.findCurrentRounds(eq(DomainType.INDUSTRY), any(LocalDate.class)))
                .thenReturn(List.of(newerRound, olderRound));
        when(assessmentRoundRepository.findCurrentRounds(eq(DomainType.VISUAL), any(LocalDate.class)))
                .thenReturn(List.of());

        scheduler.syncCurrentSurvey();

        assertThat(industryCurrent.getAssessmentRoundId()).isEqualTo(19L);
        assertThat(industryCurrent.getYearId()).isEqualTo(14L);
        assertThat(industryCurrent.isSurveyStatus()).isTrue();
    }

    @Test
    void syncCurrentSurvey_whenNoRoundMatches_marksSurveyStatusFalse() {
        CurrentSurvey current = CurrentSurvey.create(DomainType.INDUSTRY, 14L, 15L);
        current.update(14L, 15L, true);
        when(currentSurveyRepository.findByDomainType(any())).thenReturn(Optional.of(current));
        when(assessmentRoundRepository.findCurrentRounds(any(), any())).thenReturn(List.of());

        scheduler.syncCurrentSurvey();

        assertThat(current.isSurveyStatus()).isFalse();
    }

    @Test
    void syncCurrentSurvey_whenNoCurrentSurveyRowExists_createsOneBeforeSyncing() {
        when(currentSurveyRepository.findByDomainType(any())).thenReturn(Optional.empty());
        when(currentSurveyRepository.save(any(CurrentSurvey.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(assessmentRoundRepository.findCurrentRounds(any(), any())).thenReturn(List.of());

        scheduler.syncCurrentSurvey();

        // VISUAL, INDUSTRY 각각 한 번씩, 매치되는 회차가 없으니 추가 update 없이 생성만 된다.
        org.mockito.Mockito.verify(currentSurveyRepository, org.mockito.Mockito.times(2))
                .save(any(CurrentSurvey.class));
    }
}
