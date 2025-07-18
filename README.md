# spring-gift-enhancement

## Step1 PR

<details>
<summary>Click to view details</summary>

### before step1 implementation

- Member 관련 UpdateRequest 검증위치 변경 및 CustomValidator 삭제
    - 현행 `NewMemberRequestValidator` class와 `UpdateMemberRequestValidator` class에서 중복이메일검사 등의 검증작업을
      분산
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

</details>

## Step2 PR

<details>
<summary>Click to view details</summary>

### before step2 implementation

#### [refactor]

- Entity 클래스
    - [x] 엔티티(`Member`, `Product`, `Wish`)의 id setter 제거
    - [x] Member 테이블의 컬럼명 변경
        - `@Id` 어노테이션을 사용하는 `IdentifyNumber(identify_number)` 컬럼의 이름을 `id`로 변경
        - `Role` enum 타입의 `authority` 컬럼의 이름을 `role`로 변경
    - [x] Wish 테이블(기존 WishItem 클래스) 리펙터링
        - 클래스명을 `Wish`로 변경 : 테이블명인 `wish`와 클래스명인 `WishItem`의 불일치 해결 목적
        - 접근제어자 추가 : `default`(미기재)에서 `private`로 변경하여 외부에서 접근 불가하도록 조치
    - [x] 엔티티의 생성자 정리 (생성자 통폐합 및 불필요 생성자 제거)
- Dto 클래스
    - [x] `UpdateMemberResult` Dto 리팩터링
        - `UpdateMemberResponse`로 이름 변경 : Dto의 이름은 Request와 Response로 통일
        - `Optional` 타입의 `temporalPassword` 필드를 `String` 타입으로 변경 : Optional 타입은 필드 / 매개변수 / 컬렉션
          원소타입으로 사용하지 않도록 조치
- Repository 클래스
    - [x] ProductRepository 인터페이스 리펙터링
        - `findAllByDeletedIsFalseAndValidated` 메소드의 매개변수명을 `visibility`에서 컬럼명인 `validated`로 변경
    - [x] Repository 레이어의 삭제 메소드 리펙터링
        - `removeOO` 메소드를 `deleteOO`로 변경
        - 반환타입을 일괄 void형으로 변경
- Service 클래스
    - [x] Service 레이어의 `@Transactional` 어노테이션의 사용위치 변경
        - 기존 클래스 단으로 적용하던 `@Transactional` 어노테이션을 메소드별로 변경
    - [x] Service 레이어의 `throwNotFoundException` 메소드 삭제
        - MemberService, ProductService 클래스에 해당
    - [x] Service 레이어의 `deleteOO` 메소드 호출부 변경
        - hard delete 사용되는 MemberService, WishService 클래스에 해당
        - `deleteOO` 메소드가 void 타입으로 변경됨에 따라, 해당 메소드 호출부 이전에 `findOO` 메소드로 조회 후, 해당 객체가 존재하는지 여부를
          확인하는 로직 추가
    - [x] MemberService 클래스의 `generateRandomPassword` 메소드 위치 변경
        - 별도 utility 클래스로 분리하여 `PasswordUtils` 클래스에 위치

#### [fix]

- [x] Wish 테이블 수정
    - Product와의 연관관계 매핑 변경 : `@OneToOne`에서 `@ManyToOne` 어노테이션으로 변경하여, 여러명(여러 Wish)이 하나의 Product를 참조할
      수 있도록 조치

### Step2 implementation

- [x] 페이징 구현
    - API, Admin Page(Thymeleaf)에서 페이징 기능 구현
    - Admin Page 페이징 UI 구현 (assisted by AI, implemented by bootstrap)

### after step2 implementation

#### [feat]

- [x] Admin Page 전역 UI 개선 (assisted by AI, implemented by bootstrap)

#### [fix]

- [x] Admin Page 예외페이지 정상작동 구현
    - Custom Exception 및 Custom Exception Handler 구현

#### [refactor]

- [x] Page 반환방법을 Dto 사용하는 것으로 변경
    - `Page` 객체를 직접 반환하는 대신, `PageResponse` Dto로 변환하여 반환

</details>

## Step3 PR

### before step3 implementation

#### [refactor]

- [ ] 코드 시각적 리팩토링
    - Google Code Style
      적용 ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2212686575))
        - 와일드카드 없는 import문 사용과 들여쓰기 규칙 등
    - 코드 가독성
      개선 ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2212756571))
    - 코드
      단순화 ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2213658541))
- [ ] 기존 WishItem 명시된 Dto 명칭 변경 (`*WishItem*` -> `*Wish*`)
- [ ] Controller 레이어와 Service 레이어 간 데이터 전송 시 Dto 사용
  ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2212749414))
    - Controller -> Service : `객체명 Command Dto` 사용
    - Service -> Controller : `객체명 Dto` 사용
- [ ] 연관관계 매핑 시 '실제 객체' 조회 후 사용하도록
  변경 ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2212786836))
    - 설계 안정성 증대 목적. emptyOfId() 메소드 삭제
- [ ] Repository 레이어로부터 비즈니스 로직 책임 분리
  ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2213629548))
    - `existsByIdAndMemberId()`, `deleteByIdAndMemberId()` 메소드가 이에 해당
    - 단순히 id를 통해 불러오고 검증한 뒤, id를 기반으로 삭제요청하는 것으로 변경
- [ ] MemberService 클래스의 멤버정보 추가/갱신 메소드 리펙터링
  ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2213655818))
- [ ] Utility 클래스 리펙터링
  ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2213672336))
- [ ] 연관관계 어노테이션 속성 부여를 통한
  성능개선 ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2213616355))

#### [feat]

- [ ] Pageable 인자
  제한 ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2212729198))
    - Pageable 인자를 그대로 받는 대신, `PageableRequest` Dto를 사용하여 페이지네이션 요청을 제한
- [ ] 예외발생 메시지의 예외발생 항목 명시
  ([관련 PR Comment](https://github.com/next-step/spring-gift-enhancement/pull/181#discussion_r2213632458))
-

## TODO

### Whenever is ready

#### [refactor]

- [ ] application.properties 파일을 .yaml 파일로의 변경 및 리펙터링
    - `jwt.expire-length` 속성의 단위를 주석으로 명시

#### [feat]

- [ ] JPA Auditing 적용
    - soft delete 시 `deletedAt` 필드에 삭제시간 기록, deleted 필드 대체

### Wondering to apply

#### [refactor]

- [ ] Admin Page용 Service 레이어 분리
- [ ] ErrorResponse Dto의 timestamp 필드명 변경
    - 'timestamp' -> 'thrownAt' ?