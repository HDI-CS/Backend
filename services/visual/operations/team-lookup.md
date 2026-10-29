# team 매핑 조회 (API에 없음)

`UserYearRound.team`을 읽는 API가 없다. 라운드별 팀별결과 엑셀을 만들 때(예: `산업디자인_N회차_팀별결과.xlsx`,
`시각디자인_N회차_팀별결과.xlsx`) 아래 쿼리로 매번 직접 조회해야 한다.

```sql
SELECT u.id AS user_id, u.name AS user_name, uyr.team AS team
FROM user_year_round uyr
JOIN "user" u ON u.id = uyr.user_id
WHERE uyr.assessment_round_assessment_round_id = :roundId
  AND uyr.team IS NOT NULL
  AND uyr.deleted_at IS NULL
  AND u.deleted_at IS NULL;
```

- `assessment_round_assessment_round_id`: Hibernate 기본 네이밍(필드명+참조 PK 컬럼명)이라 이름이 길다
- `"user"`: 예약어라 따옴표 필요 (Postgres)

팀별결과 엑셀 생성 흐름 전체(응답 export API + 이 쿼리 결합)는
[history/work-log](../../../history/work-log/) 에 기록돼 있음.

## 근본적으로는
이 조회를 매번 수동으로 하는 게 번거로우므로, `GET /api/v1/admin/{type}/assignment/assessment/{roundId}/teams`
같은 엔드포인트를 추가하는 걸 제안했었음 (2026-10-19) — 아직 미착수.
자동화하려면 [history/harness-backlog](../../../history/harness-backlog/) 참고.
