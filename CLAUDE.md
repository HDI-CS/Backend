# HDI-CS Backend 하네스

이 파일은 목차다. 

## 이 프로젝트 정의
HDI LAB의 평가 플랫폼 백엔드. 시각디자인(VISUAL)·산업디자인(INDUSTRY) 두 도메인에 대해
전문가(평가자)가 데이터셋(이미지/제품)을 평가(정량 점수 + 정성 서술)하고, 관리자가 배정·집계·엑셀
export를 수행한다.

## 어디부터 봐야 하는지
- 사람/역할, 연락 채널 → [handbook/who](handbook/who/)
- 배포 환경, 서버, DB 접속, 도메인 구조 → [handbook/where](handbook/where/)
- 컨벤션, 작업 절차(DB 쿼리하는 법 등) → [handbook/how](handbook/how/)
- 도메인별 코드 지도·운영 매뉴얼 → [services/visual](services/visual/), [services/industry](services/industry/)
- 과거 작업 기록 / 결정 사유 / 장애 기록 → [history](history/)

## 핵심 도메인 모델 (헷갈리는 것 위주)
- `Year` : VISUAL/INDUSTRY 각각 연도(시즌) 단위. 예: "2026 3차"
- `AssessmentRound` : 한 `Year` 안의 회차(1:N). 예: "1회차", "2회차"
- `UserYearRound` : 전문가×회차 참여 기록. **team 필드가 여기 있다** (엑셀 업로드로만 채워지고,
  이를 읽어서 돌려주는 REST API는 아직 없다). 산업디자인은 회차별 "팀별결과" 엑셀 추출 도구
  (`admin/export`)가 내부적으로 이 값을 읽어 처리해준다 →
  [services/industry/operations/team-result-export.md](services/industry/operations/team-result-export.md).
  시각디자인은 아직 이 도구가 없어서 DB 직접 조회 필요 →
  [services/visual/operations/team-lookup.md](services/visual/operations/team-lookup.md)
- `CurrentSurvey` : "지금 활성화된 round가 뭔지" 캐시 테이블. 매일 00:15 KST 스케줄러가 갱신.
  **타임존 버그 있음** → [history/incidents/2026-10-20-current-survey-timezone.md](history/incidents/2026-10-20-current-survey-timezone.md)

## 리포 구조
- 이 리포(Backend)는 API 서버만. 관리자/평가자 화면은 별도 `Frontend` 리포 (Next.js, Vercel 배포, Admin은 `/admin` 서브패스)
- 두 리포 경로 → [repos](repos/)

## 작업 기록 원칙
AI가 "이건 하네스에 반영하면 좋겠다"고 판단한 내용은 바로 반영하지 말고
`history/harness-backlog/`에 쌓는다. 최종 반영 여부는 사람이 결정한다.
