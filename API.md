# API 문서

## REST API

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>URL</th>
      <th>설명</th>
      <th>권한 (Role)</th>
      <th>Request Body</th>
      <th>Response Body</th>
    </tr>
  </thead>
  <tbody>

<tr>
      <td>POST</td>
      <td>/api/members/register</td>
      <td>회원가입</td>
      <td>(Anyone)</td>
      <td><pre><code>{
  "email": "(String, NotNull, Email)",
  "password": "(String, NotNull)"
}</code></pre></td>
      <td><pre><code>{
  "token": "(String)"
}</code></pre></td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/api/members/login</td>
      <td>로그인</td>
      <td>(Anyone)</td>
      <td><pre><code>{
  "email": "(String, NotNull, Email)",
  "password": "(String, NotNull)"
}</code></pre></td>
      <td><pre><code>{
  "token": "(String)"
}</code></pre></td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/api/products</td>
      <td>상품 추가</td>
      <td>ROLE_SELLER<br>ROLE_MD</td>
      <td><pre><code>{
  "name": "(String, NotNull, max=15, pattern)",
  "price": "(Integer, NotNull, Positive)",
  "imageUrl": "(String, NotNull, max=255)"
}</code></pre></td>
      <td><pre><code>{
  "id": "(Long)",
  "name": "(String)",
  "price": "(Integer)",
  "imageUrl": "(String)",
  "validated": "(Boolean)"
}</code></pre></td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/api/products/{id}</td>
      <td>상품 조회</td>
      <td>(Anyone)</td>
      <td>없음</td>
      <td><pre><code>{
  "id": "(Long)",
  "name": "(String)",
  "price": "(Integer)",
  "imageUrl": "(String)",
  "validated": "(Boolean)"
}</code></pre></td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/api/products</td>
      <td>전체 상품 조회</td>
      <td>(Anyone)</td>
      <td>없음</td>
      <td><pre><code>[
  {
    "id": "(Long)",
    "name": "(String)",
    "price": "(Integer)",
    "imageUrl": "(String)",
    "validated": "(Boolean)"
  }
]</code></pre></td>
    </tr>
    <tr>
      <td>PATCH</td>
      <td>/api/products/{id}</td>
      <td>상품 수정</td>
      <td>ROLE_MD</td>
      <td><pre><code>{
  "name": "(String, Nullable, max=15, pattern)",
  "price": "(Integer, Nullable, Positive)",
  "imageUrl": "(String, Nullable, max=255)"
}</code></pre></td>
      <td><pre><code>{
  "id": "(Long)",
  "name": "(String)",
  "price": "(Integer)",
  "imageUrl": "(String)",
  "validated": "(Boolean)"
}</code></pre></td>
    </tr>
    <tr>
      <td>DELETE</td>
      <td>/api/products/{id}</td>
      <td>상품 삭제</td>
      <td>ROLE_MD</td>
      <td>없음</td>
      <td>없음</td>
    </tr>
<tr>
      <td>GET</td>
      <td>/api/wishlist</td>
      <td>내 위시리스트 조회</td>
      <td>(Anyone Validated)</td>
      <td>없음</td>
      <td><pre><code>[
  {
    "id": "(Long)",
    "productId": "(Long)",
    "productName": "(String)",
    "productImageUrl": "(String)",
    "deleted": "(Boolean)",
    "addedAt": "(LocalDateTime)"
  }
]</code></pre></td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/api/wishlist</td>
      <td>위시리스트에 상품 추가</td>
      <td>(Anyone Validated)</td>
      <td><pre><code>{
  "productId": "(Long, NotNull)"
}</code></pre></td>
      <td><pre><code>{
  "id": "(Long)",
  "productId": "(Long)",
  "productName": "(String)",
  "productImageUrl": "(String)",
  "deleted": "(Boolean)",
  "addedAt": "(LocalDateTime)"
}</code></pre></td>
    </tr>
    <tr>
      <td>DELETE</td>
      <td>/api/wishlist/{productId}</td>
      <td>위시리스트에서 상품 삭제</td>
      <td>(Anyone Validated)</td>
      <td>없음</td>
      <td>(No Content)</td>
    </tr>
  </tbody>
</table>

## View 렌더링

| Method | URL                  | 설명           | 권한 (Role)             | Parameters                                    | View                            |
|--------|----------------------|--------------|-----------------------|-----------------------------------------------|---------------------------------|
| GET    | /admin/products      | 상품 목록 페이지    | ROLE_MD<br>ROLE_CS    | `validated` (Boolean, optional, default=true) | `admin/product-list`            |
| GET    | /admin/products/{id} | 상품 상세 페이지    | ROLE_MD<br>ROLE_CS    | 없음                                            | `admin/product-form`            |
| PUT    | /admin/products/{id} | 상품 수정        | ROLE_MD               | 없음                                            | `redirect:/admin/products/{id}` |
| PATCH  | /admin/products/{id} | 상품 유효성 상태 변경 | ROLE_MD               | `validated` (Boolean, required)               | `redirect:/admin/products/{id}` |
| DELETE | /admin/products/{id} | 상품 삭제        | ROLE_MD               | 없음                                            | `redirect:/admin/products`      |
| GET    | /admin/products/new  | 상품 등록 페이지    | ROLE_MD               | 없음                                            | `admin/product-form`            |
| POST   | /admin/products      | 상품 등록        | ROLE_MD               | 없음                                            | `redirect:/admin/products/{id}` |
| GET    | /admin               | 관리자 메인 페이지   | (Anyone)              | 없음                                            | `admin/index`                   |
| GET    | /admin/login         | 관리자 로그인 페이지  | (Anyone)              | 없음                                            | `admin/login`                   |
| GET    | /admin/members       | 회원 목록 페이지    | ROLE_ADMIN<br>ROLE_CS | 없음                                            | `admin/member-list`             |
| GET    | /admin/members/{id}  | 회원 상세 페이지    | ROLE_ADMIN<br>ROLE_CS | 없음                                            | `admin/member-form`             |
| PATCH  | /admin/members/{id}  | 회원 정보 수정     | ROLE_ADMIN            | 없음                                            | `redirect:/admin/members/{id}`  |
| DELETE | /admin/members/{id}  | 회원 삭제        | ROLE_ADMIN            | 없음                                            | `redirect:/admin/members`       |
| GET    | /admin/members/new   | 회원 등록 페이지    | ROLE_ADMIN            | 없음                                            | `admin/member-form`             |
| POST   | /admin/members       | 회원 등록        | ROLE_ADMIN            | 없음                                            | `redirect:/admin/members/{id}`  |