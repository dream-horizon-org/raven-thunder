---
title: CI/CD
---

CI runs on PRs and `main`:
- Java 17, Maven `clean verify`
- Publishes unit test results to PRs
- Code scanning via CodeQL (compile‑only)

Releases:
- Docker images pushed to GHCR on tags
- Fat JARs attached to GitHub Releases



