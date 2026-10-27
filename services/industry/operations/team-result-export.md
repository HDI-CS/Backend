# 회차별 "팀별결과" 엑셀 추출 (산업디자인)

클라이언트가 **한 회차가 끝날 때마다** 전문가별(팀.이름 시트) 응답+정성평가+가중치 결과를 엑셀로 요청한다.
2026-10-27부터는 수동 조합(API export + DB 쿼리) 대신 아래 CommandLineRunner로 처리한다.

## 실행 방법
```bash
java -jar app.jar --spring.profiles.active=export-industry-result <assessmentRoundId> <yearId> <outputPath>

# 예시: 2026 3차 2회차
java -jar app.jar --spring.profiles.active=export-industry-result 18 14 /Users/choijeong-in/Downloads/산업디자인_3회차_2회_팀별결과_1027.xlsx
```
- `IndustryResultExportRunner` (`admin/export/runner`) + `IndustryResultExportService` (`admin/export/service`)
- `assessmentRoundId`/`yearId`/`outputPath`는 위치 인자로 받음 (하드코딩 아님, 2026-10-27에 고침)
- `ApplicationRunner` + `getNonOptionArgs()`를 쓴다 — `CommandLineRunner`로 하면 `--spring.profiles.active=...`
  옵션까지 `args[0]`에 섞여 들어와서 위치 인자 파싱이 깨진다 (실제로 겪은 버그라 이렇게 고침)

## 이 도구가 하는 일
1. `IndustryDataAssignmentRepository` — 해당 round의 평가자별 배정 데이터 목록 조회 (삭제 필터 포함)
2. `IndustryResponseRepository` — 문항별 응답(정량+정성) 조회
3. `IndustryWeightedScoreRepository` — 카테고리별 가중치 점수 조회 (한 평가자가 여러 카테고리 평가 가능)
4. `UserYearRoundRepository.findAllByAssessmentRoundId` — **team 매핑 조회.** 이전엔 이 정보를 돌려주는 API가
   없어서 매번 DB에 수동 쿼리를 날렸는데(`services/visual/operations/team-lookup.md` 참고), 이 메서드가
   생기면서 코드로 해결됨.
5. 전문가별 시트("팀.이름")로 묶어서 xlsx 생성. 카테고리 라벨(`CATEGORY_LABELS`)과 8대 평가축
   (`DIMENSION_LABELS`)은 서비스 안에 하드코딩돼 있음 — 카테고리 enum이 늘어나면 여기도 같이 추가해야 함.
6. `TEST_PR`이라는 이름의 평가자는 테스트 계정이라 결과에서 제외함 (`EXCLUDED_EVALUATOR_NAMES`)

## 아직 없는 것 / 주의
- **시각디자인(VISUAL)용은 아직 없다.** 지금은 산업디자인 전용. 시디 쪽 요청 들어오면 같은 패턴으로
  `VisualResultExportService` 만들면 됨 (가중치가 카테고리당 1세트뿐이라 더 단순함)
- 로컬 파일 경로로 직접 쓰기 때문에 **서버(EC2)에서 직접 실행해야** 결과 파일이 나온다. 로컬 개발 환경에서
  돌리면 로컬 DB를 봐야 하니 주의 (어느 DB에 붙어있는 프로파일인지 꼭 확인)
- 매 회차 끝날 때 일회성으로 실행하는 용도라 관리자 화면에 버튼으로 노출돼 있지 않음. 필요하면 REST
  엔드포인트로 옮기는 것도 고려 가능 (`history/work-log/2026-10-visual-industry-team-result-export.md` 참고)
