# Day 1 - Broken Access Control / IDOR / BOLA

## Objective

Prevent User A from accessing Order B while allowing ADMIN to access all orders.

## Test Data

User A:

```text
11111111-1111-1111-1111-111111111111
```

User B:

```text
22222222-2222-2222-2222-222222222222
```

Admin:

```text
99999999-9999-9999-9999-999999999999
```

Order B:

```text
bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb
```

## Vulnerable Test

Set:

```yaml
app:
  lab:
    vulnerable-mode: true
```

Run:

```bash
curl -i http://localhost:8080/api/orders/bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb \
  -H "X-USER-ID: 11111111-1111-1111-1111-111111111111" \
  -H "X-ROLE: USER"
```

Expected vulnerable result:

```text
200 OK
```

## Fixed Test

Set:

```yaml
app:
  lab:
    vulnerable-mode: false
```

Run the same request.

Expected fixed result:

```text
403 Forbidden
```

Admin test:

```bash
curl -i http://localhost:8080/api/orders/bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb \
  -H "X-USER-ID: 99999999-9999-9999-9999-999999999999" \
  -H "X-ROLE: ADMIN"
```

Expected:

```text
200 OK
```

## Root Cause

The vulnerable API only checks the order ID and does not verify object ownership.

## Remediation

Use object-level authorization:

```text
owner OR admin
```

Implemented in:

```text
OrderSecurity.canReadOrder(...)
OrderService.getOrder(...)
```

## Interview Notes

JWT proves identity, not object-level authorization. IDOR/BOLA happens when the backend does not check whether the authenticated user owns the requested object.
