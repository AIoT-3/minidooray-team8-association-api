# Minidooray Team 8 Association API Specification

이 문서는 Minidooray Association 서비스에서 제공하는 계정 관리 관련 API 엔드포인트와 요청/응답 구조를 상세히 설명합니다. 
이 API는 사용자 회원가입, 로그인, 정보 조회 및 상태 관리를 담당합니다.

---

## 1. 개요
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json` (모든 요청 및 응답)

---

## 2. 공통 에러 응답 형식
API 요청 처리 중 오류가 발생할 경우, 아래와 같은 공통된 JSON 형식을 반환합니다.

```json
{
  "status": 400,
  "message": "에러 메시지 내용",
  "path": "/api/endpoint/path"
}
```

- **400 Bad Request**: 입력 형식 오류 (Validation 실패 등)
- **401 Unauthorized**: 로그인 실패 (아이디/비밀번호 불일치 등)
- **404 Not Found**: 리소스를 찾을 수 없음 (사용자 미존재 등)
- **409 Conflict**: 중복된 리소스 존재 (이미 가입된 아이디 등)

---

## 3. API 상세 명세

### 3.1 회원가입 (Signup)
- **Endpoint**: `POST /accounts/signup`
- **설명**: 새로운 사용자 계정을 등록합니다.
- **Request Headers**:
  - `Content-Type: application/json`
- **Request Body**:
  | 필드명 | 타입 | 필수 여부 | 제약 사항 |
  | :--- | :--- | :---: | :--- |
  | `id` | String | O | 8~50자, 영문자 및 숫자 반드시 포함 |
  | `email` | String | O | 최대 100자, 이메일 형식 준수 |
  | `password` | String | O | 8~255자, 영문자 및 숫자 반드시 포함 |
- **Response (200 OK)**:
  ```json
  {
    "id": "testUser123",
    "status": "ACTIVE"
  }
  ```

### 3.2 로그인 (Login)
- **Endpoint**: `POST /accounts/login`
- **설명**: 사용자 인증을 수행하고 로그인 처리를 합니다.
- **Request Headers**:
  - `Content-Type: application/json`
- **Request Body**:
  | 필드명 | 타입 | 필수 여부 | 제약 사항 |
  | :--- | :--- | :---: | :--- |
  | `userId` | String | O | 가입된 사용자 아이디 |
  | `password` | String | O | 가입된 사용자 비밀번호 |
- **Response (200 OK)**:
  ```json
  {
    "userId": "testUser123"
  }
  ```
- **에러 케이스**:
  - `401 Unauthorized`: 아이디 또는 비밀번호가 일치하지 않거나, 탈퇴/휴면 상태인 경우

### 3.3 사용자 정보 조회 (Get User)
- **Endpoint**: `GET /accounts/users/{userId}`
- **설명**: 특정 사용자의 상세 정보를 조회합니다.
- **Path Variables**:
  - `userId` (String): 조회를 원하는 사용자의 ID
- **Response (200 OK)**:
  ```json
  {
    "userId": "testUser123",
    "email": "test@email.com",
    "status": "ACTIVE"
  }
  ```
- **에러 케이스**:
  - `404 Not Found`: 해당 ID를 가진 사용자가 존재하지 않는 경우

### 3.4 사용자 상태 수정 (Update Status)
- **Endpoint**: `PUT /accounts/users/{userId}/status`
- **설명**: 사용자의 상태(활성, 휴면, 탈퇴 등)를 변경합니다.
- **Path Variables**:
  - `userId` (String): 상태를 변경할 사용자의 ID
- **Request Headers**:
  - `Content-Type: application/json`
- **Request Body**:
  | 필드명 | 타입 | 필수 여부 | 제약 사항 |
  | :--- | :--- | :---: | :--- |
  | `status` | String | O | 변경할 상태 값 (예: ACTIVE, DORMANT, WITHDRAWN) |
- **Response (204 No Content)**: 응답 본문 없음
- **에러 케이스**:
  - `400 Bad Request`: 잘못된 상태 값이 입력된 경우
  - `404 Not Found`: 해당 ID를 가진 사용자가 존재하지 않는 경우
