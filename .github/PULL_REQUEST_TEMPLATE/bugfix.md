# 🐛 Bugfix PR

## 버그 수정
<!-- 어떤 버그였고, 어떻게 수정했는지 설명해주세요 -->

### 브랜치 정보
<!-- 브랜치 이름을 작성해주세요 -->
- Branch Name: `bugfix/...`

## 수정 내용
```
// 버그 수정사항을 작성해주세요
[문제 상황]
- 발생 조건:
- 증상:

[수정 내용]
- 원인:
- 해결:
```

## API 변경점
```
// API 응답이나 스펙이 변경된 경우에만 작성해주세요
[POST] /api/v1/endpoint
Content-Type: application/json

Request:
{
    // 필수값은 *로 표시
    "field"*: "string",    // 설명 추가
    "optional": "string"   // 선택값
}

Response:
Content-Type: application/json
{
    "field": "string"
}

Error:
Content-Type: application/json
{
    "code": "ERROR_CODE",    // 에러 코드
    "message": "에러 메시지"
}
```

## 시스템 변경사항
### 환경변수
```properties
// 환경변수 변경사항이 있는 경우에만 작성
```

### DB
```sql
-- DB 변경사항이 있는 경우에만 작성
```

## 참고사항
<!-- 프론트엔드 개발자나 다른 개발자들이 참고할 내용이 있다면 작성해주세요 -->
-
