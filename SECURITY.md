# Security policy

## Supported versions

Security fixes are applied to the latest version on the `main` branch.

## Reporting a vulnerability

Use GitHub's **Report a vulnerability** option in the repository Security tab. Please include the affected route or component, reproduction steps, expected impact and any safe proof of concept.

Do not open a public issue for an unpatched vulnerability. Do not include real credentials, database files, session cookies or uploaded user content in a report.

## Deployment baseline

- Set a unique administrator password through environment variables.
- Keep the H2 console disabled unless it is temporarily required.
- Terminate TLS at a trusted reverse proxy for internet-facing deployments.
- Back up the `data` and `uploads` volumes and restrict their filesystem permissions.
- Review dependency updates and run `./mvnw verify` before deployment.
