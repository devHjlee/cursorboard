# CursorBoard - AI 기반 게시판 개발 실험

이 프로젝트는 Cursor AI를 활용해 **Spring Boot + Vue 3 기반의 게시판 풀스택 시스템**을 자동 생성하고, 실사용 수준까지 얼마나 빠르게 구현 가능한지를 테스트하기 위한 실험입니다.

## 🛠 기술 스택

### ✅ Backend
- Spring Boot 3.x
- Spring Security + JWT
- JPA (Hibernate)
- MySQL
- DTO, Service, Repository 계층 구조
- GlobalExceptionHandler + JPA Auditing 적용

### ✅ Frontend
- Vue 3 + Vite + TypeScript
- Vue Router
- Pinia (상태관리)
- Axios + JWT 헤더 자동 설정
- Tailwind CSS

## 📁 프로젝트 구조

### 백엔드
```
com.cursorboard
├── config
├── security
├── user
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
├── post
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
```

### 프론트엔드
```
src/
├── views/          # 라우트 페이지: Login, PostList, PostDetail, Write, Edit
├── components/     # 재사용 UI 컴포넌트
├── store/          # Pinia 상태 관리
├── router/         # Vue Router 설정
├── api/axios.ts    # Axios 인스턴스 (JWT 자동 포함)
```

## 🧪 Cursor AI 프롬프트

### ✅ 백엔드
> Spring Boot 기반의 백엔드 게시판 API를 JPA + MySQL 환경으로 구현해주세요...

패키지 구조:
- 루트 도메인은 user, post, config, security이며,
- user, post 패키지에는 각각 다음과 같은 하위 계층이 존재합니다:
    - api: Controller
    - application: Service
    - domain: Entity
    - infrastructure: Repository

공통 개발 요구:
- 모든 컨트롤러는 @RestController로 작성해주세요.
- 요청/응답은 반드시 DTO로 구성해주세요.
- 공통 예외 처리를 위한 GlobalExceptionHandler를 구현해주세요.
- 모든 엔티티에는 createdAt, updatedAt 필드를 포함해주세요. (JPA Auditing 기반)

기능 요구 사항:

1. 사용자 인증/인가:
    - Spring Security 기반의 JWT 인증/인가 처리 적용
    - 로그인, 회원가입 API는 인증 없이 접근 가능해야 함
    - 회원가입 시 이메일 중복 체크 API 제공
    - 회원가입은 이메일, 비밀번호만 입력
    - 사용자 권한은 USER / ADMIN 두 가지

2. 게시글 관련 기능:
    - 게시글 작성, 수정, 삭제 기능
        - 로그인 사용자만 가능하며, 삭제는 작성자 또는 ADMIN 권한만 가능
    - 게시글 목록 조회: 인증 없이 접근 가능
    - 게시글 상세 조회:
        - 제목, 내용, 댓글 목록까지 포함된 단일 API 제공
        - 댓글에는 작성자 이메일, 댓글 내용 포함

3. 댓글 기능:
    - 게시글 상세 하단에 댓글 목록 출력
    - 로그인 사용자는 댓글 등록 가능
    - 삭제는 본인 또는 ADMIN만 가능

기술 스택:
- Spring Boot 3.x
- Spring Security + JWT
- JPA + MySQL
- DTO 분리 설계
- 단일 API 호출로 게시글 + 댓글 동시 반환 (상세 조회)

### ✅ 프론트엔드
> Vue 3 + Vite 기반의 TypeScript SPA 프론트엔드 프로젝트를 생성해주세요...

기본 조건:
- 전체 SPA 구조이며, Vue Router를 사용해 각 화면을 라우팅 처리해주세요.
- 상태 관리는 Pinia를 사용하고, 로그인 상태 및 JWT 토큰을 Pinia로 관리해주세요.
- Axios를 통해 백엔드와 통신하며, JWT 토큰은 Authorization 헤더에 자동 포함되도록 설정해주세요.
- Axios 설정은 별도 파일로 분리해주세요.
- 프로젝트 구조는 views/, components/, store/, router/ 디렉토리로 구성해주세요.
- 기본적인 Tailwind CSS 또는 기타 UI 스타일을 적용해주세요. 화면이 너무 밋밋하지 않게 구성해주세요.

화면 구성:
1. 로그인 페이지 (/login)
    - Email, Password 입력 필드
    - 로그인 요청 후 JWT 토큰을 localStorage에 저장
    - 로그인 성공 시 게시글 목록 페이지(/posts)로 이동

2. 게시글 목록 페이지 (/posts)
    - 전체 게시글 조회
    - 게시글 클릭 시 상세 페이지(/posts/:id)로 이동
    - 각 게시글에는 수정(/posts/:id/edit) 및 삭제 버튼 표시
    - 상단에는 게시글 등록 버튼(/posts/write) 표시

3. 게시글 상세 페이지 (/posts/:id)
    - 게시글 제목, 내용, 작성자 출력
    - 하단에 댓글 목록 출력
    - 댓글 작성 기능 포함

4. 게시글 등록 페이지 (/posts/write)
    - 제목, 내용 입력 필드
    - 저장 시 목록 페이지로 이동

5. 게시글 수정 페이지 (/posts/:id/edit)
    - 기존 게시글 내용 로드 후 수정 가능
    - 수정 완료 시 상세 페이지로 이동

6. Not Found 페이지 (/404)
    - 존재하지 않는 경로로 접근 시 표시

공통 요구사항:
- 로그인하지 않은 사용자가 보호된 라우트에 접근하면 /login으로 리디렉션 처리
- 모든 인증이 필요한 버튼(글쓰기, 수정, 삭제 등)은 로그인 상태에서만 노출
- 댓글 작성 시 로그인 상태가 아니라면 로그인 페이지로 안내
- API 요청 실패 시 사용자에게 알림 메시지를 띄우는 기본 에러 핸들링 처리

기타:
- 작동이 가능한 실제 코드로 구성해주세요.
- 컴포넌트와 페이지는 명확하게 분리하고, 파일명과 폴더명은 일관성 있게 작성해주세요.


## ⏱️ 개발 및 검증 소요 시간

| 항목 | 소요 시간 | 비고 |
|------|------------|------|
| 백엔드 검토 및 수정 | 약 2시간 | 예외처리, 보안, 구조 보완 |
| 프론트 기능 테스트 | 약 1시간 | 구조/문법 검토 없이 기능 동작만 |

## 📎 레포지토리

👉 https://github.com/devHjlee/cursorboard

## 📝 느낀 점

- Cursor AI는 실무에서 사용 가능한 코드 구조를 꽤 빠르고 안정적으로 생성해줍니다.
- 백엔드는 실무에서 많은 프로젝트를 해 왔기에 충분히 검증을 진행하면서 보완하고, vue는 단순 유지보수는 진행해봤으나 프로젝트 초기 구축부터는 경험해 본 적은 없었기에 검증에 대해서는 단순 기능동작까지만 진행 할 수 있었습니다.
- 특히 빠르게 작동 가능한 CRUD 시스템을 기획-개발까지 AI에 위임할 수 있다는 점에서, 향후 작은 서비스나 관리자 페이지에는 매우 적합하다고 느꼈습니다. (검증은 필수)
