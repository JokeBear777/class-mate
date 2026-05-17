# class-mate

## Docker Compose 실행

백엔드, 프론트엔드, MySQL, Redis를 함께 실행할 수 있습니다.

```powershell
copy .env.example .env
docker compose up --build
```

기본 접속 주소:

- Frontend: `http://localhost:${FRONTEND_PORT}`
- Backend: `http://localhost:${BACKEND_PORT}`
- Swagger: `http://localhost:${BACKEND_PORT}/swagger-ui/index.html`

프론트엔드는 Nginx로 정적 파일을 서빙합니다. `/api/` 요청은 `backend:8080/api/`로, `/ws` 요청은 `backend:8080/ws`로 프록시됩니다.

환경변수 예시는 `.env.example`을 참고하세요. 실제 비밀번호가 들어간 `.env` 파일은 Git에 올리지 않습니다.
