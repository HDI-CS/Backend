# 인증/인가

- 세션 기반 인증, `SESSION` 쿠키 사용 (단일 서버라 JWT로 안 바꿔도 당장은 충분)
- 권한: `/api/v1/admin/**` → `ROLE_ADMIN`, `/api/v1/user/**` → `ROLE_USER` (`SecurityConfig.java`)
- CORS: `allowedOriginPatterns("*")` + `allowCredentials(true)`, `Authorization` 헤더 노출 설정됨
  (`CorsConfig.java` — 주석에 "TODO: production 환경에서 설정 변경"이라고 적혀있음, 아직 안 됨)
- 세션 쿠키 관련 설정 (`application.yml`): `same-site: none`, `secure: true`, `http-only: true`
  — 프론트와 도메인이 달라서(www vs api) cross-site 쿠키 필요해서 이렇게 설정
- 원래 CORS 버그를 늦게 발견해서 프론트 쪽에서 프록시로 우회 중이었음. 프론트 개발자가 문의하면
  "그 프록시 삭제해도 된다"고 안내할 것 (백엔드 CORS 설정이 이미 커버함)

## curl로 직접 테스트할 때
- 쿠키: `Cookie: SESSION=<base64 값>`
- `Referer: https://www.hdi.ai.kr/admin/visual` 정도는 넣어주는 게 안전 (없어도 되는 듯하지만 재현 시 실제 브라우저 요청 헤더에 맞추는 게 빠름)
- 경로는 `/admin/api/api/v1/...` (프론트 프록시 경유) 아니면 `api.hdi.ai.kr/api/v1/...` (백엔드 직접) 둘 다 될 수 있음 — 세션 쿠키가 어느 쪽에서 발급됐는지에 따라 다를 수 있으니 안 되면 둘 다 시도
