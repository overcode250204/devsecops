# Day 2 - Injection

## SQL Injection Test

Vulnerable mode:

```yaml
app:
  lab:
    vulnerable-mode: true
```

Payload:

```bash
curl -i "http://localhost:8080/api/users/search?email=%27%20OR%20%271%27%3D%271"
```

Expected vulnerable result:

```text
Multiple users returned
```

Fixed mode:

```yaml
app:
  lab:
    vulnerable-mode: false
```

Expected fixed result:

```text
No matching user returned
```

## Dynamic Sort Test

Normal:

```bash
curl -i "http://localhost:8080/api/users?sortBy=email"
```

Invalid field in fixed mode:

```bash
curl -i "http://localhost:8080/api/users?sortBy=passwordHash"
```

Expected:

```text
400 Bad Request
```

## Command Injection Test

Vulnerable endpoint:

```bash
curl -i "http://localhost:8080/api/tools/ping?host=google.com"
```

Try unsafe characters:

```bash
curl -i "http://localhost:8080/api/tools/ping?host=google.com%3Bwhoami"
```

In fixed mode, invalid host should be rejected.

## Root Cause

User-controlled input reached SQL/native query or OS command execution without safe binding or allowlist validation.

## Remediation

- Use parameter binding for SQL values.
- Use allowlist for dynamic sort fields.
- Avoid shell command execution.
- If command execution is required, validate input and use `ProcessBuilder` with separated arguments.
