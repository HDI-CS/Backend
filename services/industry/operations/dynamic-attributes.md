# 산업디자인 데이터의 카테고리별 가변 스펙 (attributes)

## 배경
`IndustryData`는 청소기/헤드폰/커피머신처럼 완전히 다른 제품군을 한 테이블에 담는데, 스펙 필드가
카테고리마다 다르다. 예전엔 새 제품군이 생길 때마다 컬럼을 추가했음 (`// 2025`, `// 2026` 주석
블록 참고). 문제는:
- 18개 카테고리 중 15개(83%)가 "2026" 블록 11개 컬럼에 전부 NULL
- 새 카테고리/필드 하나 추가할 때마다 Entity + Request/Response DTO + 평가 화면 응답 +
  검색 쿼리 등 5~6개 파일을 동시에 고쳐야 했음

2026-10-27, `IndustryData`에 `attributes`(jsonb, key-value) 컬럼을 추가해서 이 문제를 해결함.
자세한 설계 논의는 [history/decisions](../../../history/decisions/)에 해당 날짜 항목 참고
(아직 별도 파일로 안 쌓였으면 이 문서가 1차 기록).

## 뭐가 바뀌었나
- **공통 필드**(제품명, 가격, 이미지 등 거의 모든 카테고리가 쓰는 것)는 그대로 고정 컬럼 유지
- **카테고리마다 다른 스펙**만 `attributes`(`Map<String, String>`, jsonb)로 이동
- 기존 "2025"/"2026" 레거시 컬럼(`noiseCancelling`, `material` 등)은 과거 데이터 조회 때문에
  안 지웠음 — 신규 데이터부터 `attributes` 사용

## 다음 회차에 새 카테고리/데이터를 넣을 때

### 1) 카테고리 "종류" 자체는 여전히 코드 등록 필요
새 제품군(예: "믹서기")이 새로 생기면:
- Backend: `IndustryDataCategory` enum에 추가 (`kr.co.hdi.domain.data.enums`)
- Frontend(`apps/admin`): `INDUSTRY_CATEGORY_CONFIG`(`adminCategoryConfig.ts`)에 그 카테고리의
  "공통 고정 필드" 목록 등록

이건 이번 변경으로 없어진 과정이 아니다. 카테고리 자체를 만드는 건 여전히 개발자 작업.

### 2) 그 카테고리만의 특수 스펙은 코드 수정 없이 관리자 화면에서
예: 믹서기의 "용량(L)", "모터출력(W)" 같은 값:
1. 관리자 화면에서 데이터 생성/수정 모달 열기
2. "추가 스펙" 섹션 → "+ 필드 추가"
3. 필드명/값 입력, 제품마다 반복 (같은 카테고리라도 제품마다 다른 스펙 가능)
4. 저장

**배포도 코드 수정도 필요 없다.**

## 알려진 제약 — 삭제 미지원 (의도적)
`IndustryData.updatePartial()`이 `attributes.putAll(request.attributes())`로 **병합만** 한다
(통째로 교체가 아님). 그래서 관리자 화면에 "삭제" 버튼을 만들어도 화면에서만 사라지고 서버에는
남는, 동작하는 척하는 버튼이 되는 문제가 있었다. "카테고리 스펙은 한 번 정해지면 거의 안 지운다"는
전제로 **삭제 기능 자체를 UI에서 뺐다.** 오타는 키 이름을 고쳐 쓰는 것으로 해결.

진짜 삭제가 필요해지면:
- `updatePartial`에서 attributes를 병합이 아니라 통째로 교체하거나
- 프론트가 "삭제할 키 목록"을 별도로 보내서 서버가 병합 후 그 키들만 명시적으로 제거

둘 다 지금은 미구현. 필요해지면 다시 설계 논의 필요.

## 관련 파일
- Backend: `domain/data/entity/IndustryData.java`, `admin/data/dto/{request,response}/IndustryData*`,
  `survey/dto/response/IndustryDataSetResponse.java`, `domain/data/repository/IndustryDataRepositoryImpl.java`
- Frontend(admin): `components/data/DataDetailModal.tsx`, `schemas/industry-data.ts`,
  `features/data/rowMeta.tsx`
- Frontend(web, 평가자 화면): `components/survey/ProductInfo.tsx`, `schemas/survey.ts`
