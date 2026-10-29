# DB 구조 핵심 (헷갈리는 테이블 위주)

- `Year` : VISUAL/INDUSTRY 각각 연도(시즌)를 생성 가능
- `AssessmentRound` (차수) : 특정 `Year`에 1차, 2차 ... N차 평가 진행 가능 (1:N)
- `UserYearRound` (회원-년도-차수) : 특정 회원이 여러 번 평가에 참여 가능해서 만든
  `AssessmentRound` × `User` 다대다 중간테이블. **`team` 필드가 여기 있다.**
  - 엑셀 업로드(`AssignmentExcelParser` → `VisualAssignmentService`/`IndustryAssignmentService`)로만 쓰여지고,
    읽어서 돌려주는 API가 없다. 필요하면 DB 직접 조회해야 함. 쿼리 예시는
    [services/visual/operations/team-lookup.md](../../services/visual/operations/team-lookup.md) 참고.

## 왼쪽 하단 보조 테이블들
- `CurrentSurvey` (현재 설문 정보) : 평가자 페이지에서 "지금 활성 round가 뭔지" 빨리 알아야 해서 캐시해 둔 테이블.
  `domainType`, `yearId`, `assessmentRoundId`를 들고 있음. **`updated_at` 컬럼이 없어서 마지막 갱신 시각을
  DB로는 못 본다** (장애 포렌식할 때 이거 때문에 막힘 — [history/incidents](../../history/incidents/) 참고)
  - 관리자 페이지에서 평가 기간을 설정하면, 시작일 12시 15분(00:15 KST 스케줄러, `CurrentSurveyScheduler`)에
    자동 갱신됨. 미리 확인하고 싶으면 스케줄 기다리지 말고 개발자가 수동으로 넣어도 됨 (연구원들이 평가 시작 전에
    미리 페이지를 확인하는 경우가 많았음)
- `CurrentVisualCategory`, `CurrentIndustryCategory` (현재 데이터셋 카테고리 정보) : 가중치 평가 화면에서
  "지금 쓰는 카테고리가 뭔지" 알아야 하는데, `VisualDataCategory`/`IndustryDataCategory` enum에는 과거 카테고리도
  다 들어있어서(관리자 페이지에서 과거 데이터 조회해야 하니까) 따로 "현재" 테이블을 둠.
  **크롤링하면서 데이터 카테고리가 정해지면 개발자가 수동으로 이 테이블에 입력해야 한다.**

## enum
- `IndustryDataCategory`, `VisualDataCategory` (`kr.co.hdi.domain.data.enums`)
- 데이터셋 카테고리가 바뀌면 enum에 **추가만** 한다. 기존 값 삭제 금지 (과거 데이터 조회가 깨짐)

## S3
- 이미지 key: `"{year}/{VI|PR}/" + UUID.randomUUID()` 형태로 저장 (업로드 파일명이 매번 달라질 수 있어서 UUID로 통일)
  - `logoImage` 컬럼: S3에 저장된 key 값
  - `originalLogoImage` 컬럼: 원본 파일명 (예: `aaa.jpg`로 업로드하면 그대로 저장)
- 이미지 다운로드 기능에서는 `logoImage`로 S3 조회하고, 응답 파일명은 `originalLogoImage`로 내려줌
- **S3에 폴더가 추가되면 `VisualData`의 `create`, `duplicate` 함수에 하드코딩된 폴더 경로를 수정해야 한다.**

## 평가 문항 수 (surveyCount)
```java
visualSurveyRepository.saveAll(surveys);
year.updateSurveyCount(surveys.size());
yearRepository.save(year);
```
화면에서 문항을 만들면 `Year.surveyCount`가 자동 갱신됨. **DB에 직접 insert하는 경우 surveyCount를 수동으로 맞춰야 한다.**
관리자 페이지 "평가 진행 상황"에서 진행중/진행완료 상태가 이상하면 이 값부터 확인.

## ERD
https://www.erdcloud.com/d/G5MyoAC5mKh8x89sw
