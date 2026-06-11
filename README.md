# Spring Boot DevSecOps Phase 1 Lab

Source base để học Giai đoạn 1 AppSec/DevSecOps với Java Spring Boot.

## Stack

- Java 21
- Spring Boot 3.3.9
- Spring Web
- Spring Data JPA
- Spring Security
- Validation
- H2 Database
- Lombok

## Cách chạy

```bash
./mvnw spring-boot:run
```

Hoặc nếu không có Maven Wrapper:

```bash
mvn spring-boot:run
```

App chạy tại:

```text
http://localhost:8080
```

H2 console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:devsecops_lab
```

User/pass:

```text
sa / password
```

## Auth tạm thời cho lab

Để tập trung vào AppSec trước, source base dùng header giả lập user login:

```http
X-USER-ID: <uuid>
X-ROLE: USER hoặc ADMIN
```

Lưu ý: Cách này chỉ dùng cho lab ngày 1, 2, 4, 5. Production không được tin header từ client.

## Seed data

User A:

```text
11111111-1111-1111-1111-111111111111
userA@example.com
ROLE USER
```

User B:

```text
22222222-2222-2222-2222-222222222222
userB@example.com
ROLE USER
```

Admin:

```text
99999999-9999-9999-9999-999999999999
admin@example.com
ROLE ADMIN
```

Order A thuộc User A:

```text
aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa
```

Order B thuộc User B:

```text
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
```

## Bật/tắt mode vulnerable

Trong `application.yml`:

```yaml
app:
  lab:
    vulnerable-mode: true
```

- `true`: mở lỗi để khai thác
- `false`: dùng code fixed

## Endpoint chính

### Health

```http
GET /api/health
```

### Day 1 - IDOR/BOLA

```http
GET /api/orders/{id}
```

Test vulnerable:

```bash
curl -i http://localhost:8080/api/orders/bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb \
  -H "X-USER-ID: 11111111-1111-1111-1111-111111111111" \
  -H "X-ROLE: USER"
```

Khi `vulnerable-mode: true`, User A đọc được Order B.

Khi `vulnerable-mode: false`, User A bị `403`.

### Day 2 - SQL Injection

```http
GET /api/users/search?email=
```

Payload:

```bash
curl -i "http://localhost:8080/api/users/search?email=%27%20OR%20%271%27%3D%271"
```

### Day 2 - Dynamic Sort

```http
GET /api/users?sortBy=email
```

### Day 4 - Mass Assignment

```http
PATCH /api/users/{id}
```

### Day 5 - SSRF

```http
POST /api/tools/fetch-url
```

## Việc bạn cần làm

1. Chạy project.
2. Test vulnerable mode.
3. Đổi `vulnerable-mode: false`.
4. Đọc code fixed.
5. Viết report trong thư mục `docs`.
6. Sau đó tự refactor theo hướng tốt hơn: `@PreAuthorize`, JWT, ZAP scan.

## Ghi chú

Source này cố tình có các nhánh vulnerable để phục vụ học tập. Không dùng làm production.
