---
date: 2026-10-20
title: CurrentSurveyScheduler가 서버 UTC 시간 기준으로 동작해 회차 자동 전환이 지연됨
type: bug
service: shared (CurrentSurvey)
status: filed
issue: https://github.com/HDI-CS/Backend/issues/167
---

## 증상
2026-10-19에 INDUSTRY 2회차(assessment_round_id=18)를 2026-10-16~10-31 기간으로 활성화되도록 날짜를
설정했으나, 2026-10-20이 되어도 자동 전환이 안 되고 이미 종료된 1회차(id=15, ~10-19)가
`current_survey`에 계속 활성 상태로 남아있었음. 수동으로 DB 값을 18로 바꿔 임시 조치.

## 원인
`CurrentSurveyScheduler.syncCurrentSurvey()`의 cron 트리거는 `zone = "Asia/Seoul"`로 00:15 KST에
맞춰 돌지만, 메서드 내부 `LocalDate.now()`는 zone 지정이 없어 **서버(컨테이너) 기본 타임존(UTC)** 기준으로
날짜를 계산한다. 컨테이너 타임존 확인됨:
```
$ docker exec hdi_be date       → Tue Oct 20 02:27:13 UTC 2026
$ docker exec hdi_be sh -c 'echo $TZ'   → (empty)
```
KST 00:15 트리거 시점 = UTC 전날 15:15라서, 내부 `today`가 실제 KST 날짜보다 하루 늦게 계산된다.
날짜 경계가 겹치는 상황(두 라운드 구간이 자정 근처에서 겹침)에서는 `findCurrentRound`가
`Optional<AssessmentRound>` 단일 결과를 기대하는데 복수 결과를 받아 예외가 날 수 있는 구조.

## 확인 못 한 부분
- `docker logs`에서 해당 시간대 예외 로그를 못 찾음 (파일 로그는 `kr.co.hdi.global.logging` 로거만 남기고
  예외는 root→콘솔로만 나가서, 콘솔 로그 보존 범위 안에서 찾아야 하는데 못 찾음)
- `CurrentSurvey`에 `updated_at`이 없어서 마지막 갱신 시각을 DB로 검증 못 함
- → 이번 건이 정확히 "동시 매치로 인한 예외" 때문이었는지는 100% 확정 못 함. 다만 타임존 버그 자체는
  코드상 명백하고 재발 가능성이 있어 이슈로 등록함.

## 조치
1. 수동으로 `current_survey`를 18로 변경해 즉시 정상화
2. 재발 방지 코드 수정 제안 (이슈 #167):
   - `LocalDate.now()` → `LocalDate.now(ZoneId.of("Asia/Seoul"))`
   - `findCurrentRound`를 `List` 기반으로 바꿔 복수 매치에도 예외 없이 동작하도록
   - (선택) `CurrentSurvey`에 `updated_at` 추가 — 다음에 또 이런 일 생기면 이것부터 확인 가능하게
   - (선택) 스케줄러 성공/실패를 명시적으로 로깅
