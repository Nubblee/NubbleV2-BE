# ✨ Feature PR

## 기능 설명
<!-- 이 기능이 무엇인지 설명해주세요 -->

### 브랜치 정보
<!-- 브랜치 이름을 작성해주세요 -->
- Branch Name: `feature/...`

## 변경 내용
```
// 구현된 기능을 작성해주세요
- 
```

## API 명세
<!-- 프론트엔드 개발자가 사용할 API 정보를 작성해주세요 -->
```
[POST] /api/v1/endpoint
Request:
{
    // 필수값은 *로 표시
    "field"*: "string",    // 설명 추가
    "optional": "string"   // 선택값
}

Response:
{
    "field": "string"
}

Error:
{
    "code": "ERROR_CODE",    // 에러 코드
    "message": "에러 메시지"
}
```

## 시스템 변경사항
### 환경변수
```properties
// 환경변수 변경사항이 있는 경우에만 작성
NEW_VAR=value
```

### DB
```sql
-- DB 변경사항이 있는 경우에만 작성
CREATE TABLE table_name (
                            id BIGINT PRIMARY KEY
);
```

## 참고사항
<!-- 프론트엔드 개발자나 다른 개발자들이 참고할 내용이 있다면 작성해주세요 -->
-
