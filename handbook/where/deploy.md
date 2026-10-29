# 배포 / 인프라

## 호스트
- 프론트(관리자+평가자): https://www.hdi.ai.kr (`/admin` 서브패스가 관리자 화면, basePath 그대로 사용)
- 백엔드 API: https://api.hdi.ai.kr
- Swagger: https://api.hdi.ai.kr/swagger-ui/index.html (`/v3/api-docs`에서 전체 경로 목록 JSON으로 확인 가능)

주의: 프론트(Next.js, Vercel)가 백엔드를 프록시할 때 경로가 `/admin/api/api/v1/...` 로 **api가 두 번** 들어간다.
직접 curl로 호출할 때 자주 틀리는 지점.

## 서버 구성
- SpringBoot 3.5.5, PostgreSQL 17.4, Redis
- 아키텍처: 단일 서버 (ALB - EC2 - RDS), Redis는 아직 이중화 안 함
- RDS는 private subnet, EC2는 public subnet에 Bastion 겸용으로 배치 (별도 Bastion 안 만듦)
- VPC: Public Subnet 4개 / Private Subnet 4개, 가용영역 4개 전부 사용

## 컨테이너
- docker-compose로 `hdi_be`(app), `redis`(cache) 두 컨테이너 운영
- 이미지: `gothax2/hdi_be:latest` (최초 개발자 Docker Hub 계정 그대로 사용 중, 문제 없음)
- 로그 볼륨: `./logs:/app/logs`
- 서버 접속: `ubuntu@ip-172-31-6-135` (AWS EC2, ap-northeast-2 추정)
- 컨테이너 내부 명령은 `sudo docker exec hdi_be ...` (docker 그룹 권한 없으면 sudo 필요)
- **컨테이너 타임존은 UTC** (`TZ` 환경변수 비어있음, `date` 찍으면 UTC로 나옴) — 날짜 비교 로직 짤 때 KST 명시 안 하면 버그남

## 로깅
- `logback-spring.xml`: `kr.co.hdi.global.logging` 로거만 파일(`/app/logs/api.log`, 날짜별 롤링, 60일 보관)에 남는다.
- **그 외 로그(예외 스택트레이스 포함)는 root 로거 → 콘솔로만 나간다.** 파일 로그에 없다고 장애가 없었던 게 아님.
  `docker logs hdi_be --since ... --until ...`로 콘솔 로그를 봐야 한다.
- API 로그 포맷(`ApiLoggingAspect`): `[API] SUCCESS|FAIL | userId=... | method=... | uri=... | elapsed=Nms`
  - `elapsed`는 서버 처리 시간이지 사용자 작업 시간이 아님. 작업 간격을 보려면 로그 타임스탬프 간 차이를 써야 함.
  - 로그 타임스탬프는 **초 단위**까지만 (ms 없음)

## CI/CD
- GitHub Actions로 이미지 빌드/push. 레지스트리를 바꾸면 GitHub Actions secret 값도 같이 수정해야 함.
- 디스크 정리: `sudo docker image prune -f`

## ERD
- https://www.erdcloud.com/d/G5MyoAC5mKh8x89sw
