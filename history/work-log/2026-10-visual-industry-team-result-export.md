---
date: 2026-10-19 ~ 2026-10-20
title: 시각디자인/산업디자인 "N회차 팀별결과" 엑셀 재생성 흐름 정리
type: work-log
service: visual, industry
---

## 배경
관리자가 평가 결과를 "팀별결과" 엑셀(전문가별 시트, 문항 점수+정성평가+가중치 카테고리 요약)로
받아서 쓰고 있었는데, 이게 어떤 API/스크립트로 만들어진 건지 기록이 없어서 매번 수동으로 재구성해야 했음.

## 알아낸 것
1. 응답/가중치 데이터는 정식 API로 받을 수 있다:
   `GET /api/v1/admin/{type}/evaluations/assessment/{roundId}/datasets/export`
   (`type` = VISUAL|INDUSTRY, 대문자) → `qualitative_answers` + `weighted_scores` 두 시트짜리 xlsx
2. **team 정보는 이 API에도, 다른 어떤 API에도 없다.** `UserYearRound.team`은 엑셀 업로드로만
   쓰여지고 읽는 API가 없음 → DB 직접 조회 필요 (쿼리는
   [services/visual/operations/team-lookup.md](../../services/visual/operations/team-lookup.md))
3. 산업디자인은 한 전문가가 여러 제품 카테고리를 평가할 수 있어서 `weighted_scores`에 한 유저당
   여러 행(카테고리별)이 나온다. 시각디자인은 유저당 카테고리 1개.
4. 카테고리 요약 행 순서는 고정 순서(에어프라이어→커피그라인더→커피머신→커피메이커→믹서→멀티쿠커→
   압력밥솥)를 따라야 기존 파일과 동일한 결과가 나옴. API 응답 자체의 순서는 보장 안 됨.
5. "N회차"라는 파일명은 `AssessmentRound.folderName`(회차)이 아니라 **Year 폴더명**("2026 3차")을
   가리킴. round 번호랑 헷갈리지 말 것.

## 관리자 API 호출 메모
- 세션 쿠키로 직접 curl할 때 경로는 `https://www.hdi.ai.kr/admin/api/api/v1/admin/{TYPE}/...`
  (api가 두 번 들어감, [handbook/where/deploy.md](../../handbook/where/deploy.md) 참고)
- `type`은 대문자로 (`VISUAL`, `INDUSTRY`) — 소문자로 하면 401/403 남

## 남은 일
- team 조회를 API로 노출하는 엔드포인트 추가 제안함 (미착수) — 되면 이 전체 흐름이 세션 쿠키 +
  round 번호만으로 완전 자동화 가능
