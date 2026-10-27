# DB에 쿼리하는 법

운영 DB는 AWS RDS(PostgreSQL 17.4), private subnet에 있음. 직접 접속 수단이 따로 정리된 스크립트는
아직 없고, 지금까지는 DBeaver 등 GUI 클라이언트로 접속해서 수동 실행해왔다.

- 테이블/컬럼명은 Hibernate 기본 네이밍을 따른다 (필드명 그대로 snake_case, FK는
  `필드명_참조테이블PK컬럼명` 형태로 길어짐 — 예: `assessment_round_assessment_round_id`)
- `user` 테이블은 예약어라 쿼리에서 꼭 큰따옴표로 감싸야 함: `"user"`
- 자주 쓰는 쿼리 모음: [handbook/where/db.md](../where/db.md), [services/visual/operations](../../services/visual/operations/)

## AI(Claude 등)에게 DB 조회를 맡길 때
세션에 DB 자격증명을 직접 쥐어주지 않는다. **사람이 쿼리를 실행하고 결과만 붙여넣는 방식**을 기본으로 한다.
(이유: 프로덕션 DB 자격증명을 대화 컨텍스트에 남기지 않기 위함)
