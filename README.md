# spring-gift-enhancement

## Step1 PR
### before step1 implementation
- Member 관련 UpdateRequest 검증위치 변경 및 CustomValidator 삭제 
  - 현행 `NewMemberRequestValidator` class와 `UpdateMemberRequestValidator` class에서 중복이메일검사 등의 검증작업을 분산 
  - 중복이메일검사의 경우 Service 레이어에서 수행하도록 조치
  - 이메일양식 검사 및 비밀번호길이검사의 경우 Dto에서 수행하도록 조치
- `@LoginMember` 반환타입을 Dto로 변경
  - 현행 `Member` 엔티티를 직접 반환하는것 대신, `AuthenticatedMember` Dto로 반환
  - 불필요한 Member 엔티티 정보 노출 방지 기대
### Step1 implementation
- JPA 엔티티 매핑
  - Repository 레이어의 추상화 및 메소드명 변경
  - Repository 레이어의 메소드명 변경에 따른 Service 레이어의 호출부 수정
  - Entity 클래스의 어노테이션 추가 및 update 메소드의 setter로의 변환 (불변객체 미반환화)
  - 테스트코드 작성