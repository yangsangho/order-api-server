# 주문 관리 API 서버

### 버전 정보

- Spring Boot : 3.1.0
    - [Dependency 버전](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html#appendix.dependency-versions)
    - Spring Framework : 6.0.9
    - Spring Data JPA : 3.1.0
        - Hibernate : 6.2.2.Final
    - Spring Rest Docs : 3.0.0
- Java : 17
- QueryDsl : 5.0.0
- Mockito : 5.3.1
- Jacoco : 0.8.10
- Lombok : 1.18.26
- H2 : 2.1.214
- ULID Creator : 5.2.0

### API 요구사항 분석

구체적인 요구사항이 없어서, <u>임의로 요구사항을 설정</u>했습니다.

- 주문 접수란?
    - 사용자가 특정 상품들을 담아서 주문(요청)하는 것을 `주문 접수`로 간주한다.
- 주문 완료란?
    - 사용자가 주문한 것들을 결제하는 것을 `주문 완료`로 간주한다.
- 인증 없이 개발하기 위해, `주문 접수처리`를 제외한 나머지 API에서 사용자에 대한 정보를 받지 않습니다.
    - 주문자 정보를 확인하기 위해 `주문 접수처리`에선 사용자 ID를 받습니다.
    - 누구나 특정 주문에 대해 완료 처리를 수행할 수 있다.
    - 누구나 모든 주문 목록을 조회할 수 있다.
- 주문
    - 주문할 상품 정보, 주문 상태, 주문 일시, 주문자 정보, 배송 정보를 포함한다.
    - 회원(사용자)만 주문이 가능하다.
    - 배송 정보는 회원과 관계없이, 따로 주문에 종속된 정보이다.
    - 결제 금액에 대한 정보를 확인할 수 있다.
    - 배송비와 할인이 있다.
        - 배송비 : 서울이면 0원. 나머지 지역은 3,000원
        - 할인 : 전체 주문 상품 수량이 5개 이상이면, 총 상품금액의 10% 할인 (배송비 제외)
    - 주문 상태
        - `접수`, `완료`
    - 주문자 정보
        - `이름`, `전화번호`
    - 배송 정보
        - `이름`, `주소`, `전화번호`, `따로 전달할 메세지`
    - 주문 상품
        - `상품`, `개수`(최대 999)
        - 상품
            - `이름`, `가격`(최대 1억원)
- 결제
    - 결제 수단에 대한 정보를 가지고 있다.
    - 결제 수단 별로 별도의 API를 만들지 않고, 하나의 API로 모든 결제를 진행하는 걸로 가정한다.
        - 결제는 외부 서비스를 이용하며, 항상 성공하는 것으로 가정한다.
    - 결제 이후에 상품 가격이나 할인 정책 및 배송비 정책이 달라질 수 있으니, 결제한 금액 정보를 함께 기록한다.
    - 결제 수단 정보
        - 수단 : `카드결제`, `간편결제`, `가상계좌`, `휴대폰결제`
        - 수단 데이터 (JSON)
            - 각 수단 별로 필요한 데이터
                - ex) 카드 결제라면 카드 번호, cvc 등의 데이터
            - 여기선 따로 각 수단 별 데이터들을 정의하지 않고, `"{}"`로 통일합니다.

| API 이름  | 요구사항                                                                                                                                                     | Endpoint                           | Request Data                                               | Response Data                                                      |
|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|------------------------------------------------------------|--------------------------------------------------------------------|
| 주문 접수처리 | - 사용자가 특정 상품들을 담아서 주문한다.<br/>- 주문이 접수된 이후부터 조회가 가능하다.                                                                                                    | [POST] /orders                     | 주문자 id,<br/>상품 목록(상품 id & 수량)<br/>배송 정보(이름, 주소, 전화번호, 메세지) | 주문 id                                                              |
| 주문 완료처리 | - 특정 주문에 대한 결제를 진행한다.<br/>- 이미 완료된 주문이라면, 무시한다.                                                                                                          | [POST] /orders/{order-id}/complete | 결제 수단                                                      |                                                                    | 
| 단일 주문조회 | - 하나의 주문에 대한 정보를 조회한다.<br/>- 결제 전이라면 결제 예정 금액을, 결제 후라면 결제한 금액 정보를 전달한다.                                                                                  | [GET] /orders/{order-id}           |                                                            | 주문 번호, 주문 상태, 주문 상품,<br/>결제 정보(금액,상태,수단), 주문 일시,<br/>주문자 정보, 배송 정보 | 
| 주문 목록조회 | - 모든 주문 목록을 조회한다.<br/>- 페이지네이션을 적용한다.<br/>- 정렬 조건 : 주문 일시, 배송 주소<br/>- 필터 조건 : 주문 상태<br/>- 대표 상품 이름은 첫번째 상품 이름에<br/>나머지 상품의 개수를 합쳐 만든다.<br/>ex) 선풍기 외 3개 | [GET] /orders                      | 페이지 번호, 목록 개수,<br/>정렬 조건, 필터 조건                            | 대표 상품 이름, 배송지, 주문 상태<br/>결제 금액, 주문 일시                              |

### DB 스키마

![erd](https://github.com/yangsangho/order-api-server/assets/44158921/987dcd57-5627-4467-993d-25c8bb864a2d)

- [schema.sql](src/main/resources/sql/schema.sql)

### API 명세서

Spring Rest Docs를 활용해서 작성했습니다.  
로컬 환경에서 실행 후, [http://localhost:8080/docs/index.html](http://localhost:8080/docs/index.html) 에서 확인할 수 있습니다.
<img width="843" alt="docs1" src="https://github.com/yangsangho/order-api-server/assets/44158921/307d57bb-5176-497d-bfa6-9ad025c8ad87">
<img width="842" alt="docs2" src="https://github.com/yangsangho/order-api-server/assets/44158921/ffecc128-0179-4507-b463-b050fa8859b6">
<img width="838" alt="docs3" src="https://github.com/yangsangho/order-api-server/assets/44158921/72cce26b-bad4-4e75-bbee-660dbcb41cfa">
<img width="836" alt="docs4" src="https://github.com/yangsangho/order-api-server/assets/44158921/934d9088-4d74-449a-945c-6fb6faac3a90">
<img width="836" alt="docs5" src="https://github.com/yangsangho/order-api-server/assets/44158921/2fd7026f-f4af-4361-b0d6-007be70a8014">

### 테스트

유닛테스트를 작성하고, Jacoco를 활용해 Test Coverage를 측정했습니다.
<img width="1146" alt="test_coverage" src="https://github.com/yangsangho/order-api-server/assets/44158921/6ea74470-8066-4fff-be60-abb124718c4f">

### 실행 및 확인

- JDK 버전 17 이상이라면, 별도의 설정 없이 실행해서 확인해볼 수 있습니다.
- 실행 시 사용할 초기 더미 데이터가 있습니다. [data.sql](src/main/resources/sql/data.sql)
    - 아래의 ID를 참조해서, 주문 접수 Request Body를 만들 수 있습니다.
    - member id
        - 01887837-426e-d5fb-b9af-4cbc3619f044
        - 0188786b-a80f-904c-5276-087998d7d930
    - product id
        - 01887839-e8f1-ed2f-c023-e4f629ebd8cd
        - 0188783a-b1c0-88fd-9e79-787ad03616bb
        - 0188783b-08dc-d8ac-c345-8a9e211afd5e
- [PostMan](https://documenter.getpostman.com/view/16974043/2s93mBxKVV)에 접속해 Import 후 테스트를 직접 해볼 수 있습니다.
