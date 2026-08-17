# ExamFlow

[![CI](https://github.com/TrieuNguyenPhu/examflow/actions/workflows/ci.yml/badge.svg)](https://github.com/TrieuNguyenPhu/examflow/actions/workflows/ci.yml)

ExamFlow is a self-hosted online assessment application for small institutions and training teams. Administrators create timed exams, manage question sets and review submissions. Students complete exams in a focused interface, receive an immediate score and can review every recorded answer.

## Highlights

- Role-based administrator and student workspaces
- Exam and multiple-choice question management
- Timed exam sessions with server-side submission-window checks
- One-attempt enforcement at both application and database levels
- Immediate results, answer review and progress history
- CSRF protection, BCrypt password hashing and security headers
- Validated, re-encoded JPEG/PNG profile uploads with metadata removed
- Responsive, keyboard-friendly interface with no frontend CDN dependency
- Environment-driven administrator bootstrap and runtime configuration
- Automated Maven tests and GitHub Actions CI

## Technology

- Java 21
- Spring Boot 4.1
- Spring Security 7
- Spring Data JPA and Hibernate
- Thymeleaf
- H2 file database for local and small self-hosted deployments
- Maven Wrapper

## Run locally

Requirements: Java 21 or newer. Maven does not need to be installed.

Set an initial administrator account before the first start. ExamFlow does not ship with a default password.

PowerShell:

```powershell
$env:APP_ADMIN_USERNAME='admin@example.com'
$env:APP_ADMIN_PASSWORD='replace-with-at-least-12-characters'
$env:APP_ADMIN_NAME='Administrator'
.\mvnw.cmd spring-boot:run
```

Bash:

```bash
export APP_ADMIN_USERNAME='admin@example.com'
export APP_ADMIN_PASSWORD='replace-with-at-least-12-characters'
export APP_ADMIN_NAME='Administrator'
./mvnw spring-boot:run
```

Open <http://localhost:7890>. The bootstrap variables are only used to create the administrator when that username does not already exist.

### Demo workspace

For a clean local workspace with programming and DevOps assessments, run the isolated `demo` profile:

```powershell
$env:SPRING_PROFILES_ACTIVE='demo'
.\mvnw.cmd spring-boot:run
```

It creates five technical exams with 30 questions in `data/examflow-demo` and leaves the default database untouched. The seed is idempotent, so restarting does not duplicate data.

| Role | Email | Password |
| --- | --- | --- |
| Administrator | `admin@examflow.local` | `ExamFlowDemo2026` |
| Student | `trieu@examflow.local` | `ExamFlowDemo2026` |

The credentials are for local demo use only. Never enable the `demo` profile in a deployed environment. To reset or roll back the demo data, stop the application and remove only the `data/examflow-demo` database files.

### Configuration

Copy [`.env.example`](.env.example) as a reference. Important variables:

| Variable | Default | Purpose |
| --- | --- | --- |
| `APP_ADMIN_USERNAME` | empty | Initial administrator username |
| `APP_ADMIN_PASSWORD` | empty | Initial administrator password, minimum 12 characters |
| `SERVER_PORT` | `7890` | HTTP port |
| `DATABASE_URL` | `jdbc:h2:file:./data/examflow;AUTO_SERVER=TRUE` | JDBC connection URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | empty | Database password |
| `UPLOAD_DIR` | `./uploads` | Profile image directory |
| `H2_CONSOLE_ENABLED` | `false` | Enables the administrator-only H2 console when explicitly needed |

Runtime databases, uploaded files, local environment files and logs are excluded from Git.

## Test and build

```bash
./mvnw verify
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

Build and run the packaged application:

```bash
./mvnw package
java -jar target/examflow-1.0.0-SNAPSHOT.jar
```

## Docker

```bash
docker build -t examflow .
docker run --rm -p 7890:7890 \
  -e APP_ADMIN_USERNAME=admin@example.com \
  -e APP_ADMIN_PASSWORD=replace-with-at-least-12-characters \
  -v examflow-data:/app/data \
  -v examflow-uploads:/app/uploads \
  examflow
```

Alternatively, set the required variables in your shell and run `docker compose up --build`.

## Security

Please report vulnerabilities through GitHub's private vulnerability reporting flow. See [SECURITY.md](SECURITY.md). Do not publish credentials, database files or user uploads in issues.

## License and origin

ExamFlow is available under the MIT License. It is derived from an earlier MIT-declared Online Exam System project and substantially revises its build, security, tests, documentation and interface. See [LICENSE](LICENSE) and [NOTICE](NOTICE) for details.
