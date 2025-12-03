# Contributing to Thunder

First off, thank you for considering contributing to Thunder! It's people like you that make Thunder such a great tool.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Development Process](#development-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Documentation](#documentation)
- [License](#license)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. We pledge to make participation in our project a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, gender identity and expression, level of experience, nationality, personal appearance, race, religion, or sexual identity and orientation.

### Our Standards

**Examples of behavior that contributes to creating a positive environment include:**

- Using welcoming and inclusive language
- Being respectful of differing viewpoints and experiences
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

**Examples of unacceptable behavior include:**

- The use of sexualized language or imagery and unwelcome sexual attention or advances
- Trolling, insulting/derogatory comments, and personal or political attacks
- Public or private harassment
- Publishing others' private information without explicit permission
- Other conduct which could reasonably be considered inappropriate in a professional setting

### Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be reported by contacting the project maintainers. All complaints will be reviewed and investigated and will result in a response that is deemed necessary and appropriate to the circumstances.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the [issue list](https://github.com/dream-horizon-org/thunder/issues) as you might find out that you don't need to create one.

When creating a bug report, please include as many details as possible:

#### Before Submitting a Bug Report

- **Check the documentation** - The issue might be covered in the [docs](https://dream-horizon-org.github.io/raven-thunder/)
- **Check existing issues** - The bug might already be reported
- **Check recent changes** - Verify if the unexpected behavior is a result of a recent, intended feature update.

#### How to Report a Bug

1. **Use a clear and descriptive title**
2. **Describe the exact steps to reproduce the problem**
3. **Provide specific examples** to demonstrate the steps
4. **Describe the behavior you observed** after following the steps
5. **Explain which behavior you expected** to see instead and why
6. **Include relevant logs** and error messages
7. **Include environment details**:
   - Java version
   - Maven version
   - Docker version (if applicable)
   - Operating system
8. **Include screenshots** if applicable

---

### 🐞 Bug Report Template

#### Describe the bug

A clear and concise description of what the bug is.

#### To Reproduce

Steps to reproduce the behavior:

1. Go to '...'
2. Click on '...'
3. Scroll down to '...'
4. See error

#### Expected behavior

A clear and concise description of what you expected to happen.

#### Screenshots

If applicable, add screenshots to help explain your problem.

#### Environment

- **OS:** e.g. macOS 14.0
- **Java Version:** e.g. 17.0.1
- **Maven Version:** e.g. 3.9.0
- **Thunder Version:** e.g. 1.0.0-SNAPSHOT

#### Additional context

Add any other context about the problem here.

---

### Suggesting Enhancements

Enhancement suggestions are tracked as [GitHub issues](https://github.com/dream-horizon-org/thunder/issues). When creating an enhancement suggestion, make sure to:

1. **Use a clear and descriptive title**
2. **Provide a step-by-step description** of the suggested enhancement
3. **Provide specific examples** to demonstrate the steps
4. **Describe the current behavior** and explain which behavior you expected to see instead
5. **Explain why this enhancement would be useful**
6. **List any alternatives** you've considered

### 🌟 Enhancement Template

#### Is your feature request related to a problem? Please describe.

A clear and concise description of what the problem is.  
Example: *I'm frustrated when [...] because [...].*

#### Describe the solution you'd like

A clear and concise description of what you want to happen.  
Example: *It would be great if Thunder could [...].*

#### Describe alternatives you've considered

A clear and concise description of any alternative solutions or features you've considered.  
Example: *I've tried [...], but it doesn't fully solve the issue because [...].*

#### Additional context

Add any other context or screenshots about the feature request here.  
You may include code snippets, mockups, or configuration details if relevant.

---

### Pull Requests

Pull requests are welcome! Please follow these guidelines:

1. **Fork the repository** and create your branch from `main` or `master`
2. **Follow coding standards** ([see below](#coding-standards))
3. **Add tests** for new functionality
4. **Update documentation** for API changes
5. **Ensure the test suite passes**
6. **Make sure your code lints** (Spotless and Checkstyle)
7. **Update CHANGELOG.md** (if applicable)
8. **Issue that pull request!**

## Development Setup

### Prerequisites

- **Java 17** (JDK) - [Download](https://adoptium.net/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **Docker** ≥ 20.10 - [Download](https://www.docker.com/products/docker-desktop/)
- **Docker Compose** ≥ 2.0
- **IDE** (IntelliJ IDEA (recommended), Eclipse, or VS Code)

### Setting Up Development Environment

#### 1. Fork and Clone

Fork the repository on GitHub, then run:

```bash
git clone https://github.com/YOUR_USERNAME/thunder.git
cd thunder
git remote add upstream https://github.com/dream-horizon-org/thunder.git
```

#### 2. Build the Project

```bash
mvn clean install
```

#### 3. Run Tests

Run all tests:

```bash
mvn clean verify
```

Run specific test class:

```bash
mvn -q -pl thunder-admin -Dtest=AdminServiceImplCreateCTATest test
```

Run integration tests:

```bash
# Requires Docker running
mvn -pl thunder-api -am verify
```

#### 4. Start Services

Start all the components of Thunder:

```bash
./scripts/start.sh
```

Stop all the components of Thunder:

```bash
./scripts/stop.sh
# or
docker-compose down
```

### IDE Setup

#### IntelliJ IDEA

1. Open the project
2. Import Maven project (auto-detected)
3. Set JDK 17 as project SDK
4. Configure code style: Settings → Editor → Code Style → Java → Import → Google Style
5. Install "Google Java Format" plugin
6. Enable "Reformat code on save"

#### Eclipse

1. Import as Maven project
2. Set Java 17 as JRE
3. Install Google Java Format plugin

#### VS Code

1. Install Java Extension Pack
2. Install "Google Java Format" extension
3. Enable format on save

## Development Process

### Branch Naming

- `feature/description` - New features
- `bugfix/description` - Bug fixes
- `docs/description` - Documentation updates
- `refactor/description` - Code refactoring
- `test/description` - Test additions/updates

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/) format:

```text
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**

```text
feat(admin): add CTA status transition endpoint

Add PUT endpoint for transitioning CTA status from draft to live.
Includes validation and state machine updates.

Closes #123
```

```text
fix(api): resolve state machine snapshot merge issue

Fix issue where state machine snapshots were not properly merged.
Added additional validation checks.

Fixes #456
```

### Workflow

1. **Create a branch** from `main` or `master`

   `git checkout -b feature/my-feature`

2. **Make your changes**

   - Write code
   - Add tests
   - Update documentation
   - Format code: `mvn spotless:apply`

3. **Commit your changes**

   `git add .`
   `git commit -m "feat: add new feature"`

4. **Keep your branch updated**

   `git fetch upstream`
   `git rebase upstream/main`

5. **Push to your fork**

   `git push origin feature/my-feature`

6. **Create Pull Request**

   - Go to GitHub
   - Click "New Pull Request"
   - Select your branch
   - Fill out the PR template

## Coding Standards

### Java Style Guide

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Keep methods focused and concise (max 50 lines recommended)
- Use JavaDoc for public APIs
- Avoid deep nesting (max 3 levels)

### Code Formatting

We use **Spotless** with Google Java Format. Configure your IDE:

**IntelliJ IDEA:**

1. Install "Google Java Format" plugin
2. Enable "Reformat code on save"

**VS Code:**

1. Install "Google Java Format" extension
2. Enable format on save

**Manual Formatting:**

```bash
mvn spotless:apply
```

**Check Formatting:**

```bash
mvn spotless:check
```

### Code Quality

- **No warnings**: Fix all compiler warnings
- **No TODO comments**: Remove or implement TODOs
- **Meaningful names**: Use descriptive variable and method names
- **DRY principle**: Don't Repeat Yourself
- **Single Responsibility**: Each class/method should do one thing
- **Error handling**: Handle errors appropriately
- **Logging**: Use appropriate log levels (SLF4J/Logback)

### Code Linting

We use **Checkstyle** for code style validation:

```bash
mvn checkstyle:check
```

The CI pipeline will automatically run:
- `mvn spotless:check` - Formatting validation
- `mvn checkstyle:check` - Style validation

### Example

```java
/**
 * Creates a new CTA for the specified tenant.
 *
 * @param tenantId the tenant identifier
 * @param cta the CTA create request
 * @param user the user performing the operation
 * @return Single emitting the generated CTA ID
 * @throws DefinedException if validation fails or duplicate name exists
 */
public Single<Long> createCTA(String tenantId, CTARequest cta, String user) {
  // Implementation
}
```

## Testing Guidelines

### Test Structure

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions (suffix: `*IT.java`)
- **Test Naming**: `MethodName_StateUnderTest_ExpectedBehavior`

### Writing Tests

```java
@Test
void createCTA_WithValidRequest_ReturnsCTAId() {
  // Given
  String tenantId = "default";
  CTARequest request = buildValidCTARequest("Test CTA");
  String user = "test@example.com";

  // When
  Long ctaId = adminService.createCTA(tenantId, request, user).blockingGet();

  // Then
  assertNotNull(ctaId);
  assertTrue(ctaId > 0);
}
```

### Test Coverage

- Aim for **80%+ code coverage**
- Test happy paths
- Test error cases
- Test edge cases
- Test boundary conditions

### Running Tests

```bash
# Run all tests
mvn clean verify

# Run tests for specific module
mvn -q -pl thunder-admin test

# Run specific test class
mvn -q -pl thunder-admin -Dtest=AdminServiceImplCreateCTATest test

# Run integration tests
mvn -pl thunder-api -am verify
```

## Pull Request Process

### Before Submitting

- Code follows style guidelines
- All tests pass
- New tests added for new functionality
- Documentation updated
- CHANGELOG.md updated (if applicable)
- No merge conflicts with main
- Branch is up to date with main
- Code formatted: `mvn spotless:apply`
- Code style checked: `mvn checkstyle:check`

### PR Template

```markdown
## Description

Brief description of changes

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing

- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed

## Checklist

- [ ] Code follows style guidelines
- [ ] Code formatted with Spotless
- [ ] Checkstyle passes
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests added and passing
- [ ] Dependent changes merged

## Related Issues

Closes #123
```

### Review Process

1. **Automated Checks**: CI/CD runs tests, linting (Spotless, Checkstyle), and security scans
2. **Code Review**: At least one maintainer reviews (required for merge)
3. **Feedback**: Address review comments
4. **Approval**: Maintainers approve PR
5. **Merge**: Squash and merge to main (maintainers only)

**Note**: Direct commits to main are strictly prohibited. All changes must go through pull requests.

## Documentation

### Code Documentation

- **JavaDoc**: All public APIs must have JavaDoc
- **Inline Comments**: Explain "why", not "what"
- **README Updates**: Update README for user-facing changes
- **API Docs**: Update API contract documentation for API changes

### Documentation Updates

When adding features:

- Update relevant documentation in `docs/docs/`
- Add examples if applicable
- Update API reference if API changed
- Update configuration guide if config changed
- Update Core Entities guide if new entities added

### Running Documentation Locally

```bash
cd docs
npm install
npm run start
# open http://localhost:3000
```

## Project Structure

```
thunder/
├── thunder-core/              # Core models, DAOs, and client implementations
│   ├── src/main/java/
│   │   └── com/dream11/thunder/core/
│   │       ├── dao/          # Data access objects
│   │       ├── model/        # Domain models
│   │       ├── io/           # Request/Response DTOs
│   │       └── util/         # Utility classes
│   └── src/test/java/        # Test code
├── thunder-api/               # SDK and Debug REST APIs (port 8080)
│   ├── src/main/java/
│   │   └── com/dream11/thunder/api/
│   │       ├── rest/         # REST endpoints
│   │       ├── service/      # Business logic
│   │       └── io/           # Request/Response DTOs
│   └── src/test/java/
├── thunder-admin/             # Admin panel REST APIs (port 8081)
│   ├── src/main/java/
│   │   └── com/dream11/thunder/admin/
│   │       ├── rest/         # REST endpoints
│   │       ├── service/      # Business logic
│   │       └── io/           # Request/Response DTOs
│   └── src/test/java/
├── docs/                      # Docusaurus documentation site
│   ├── docs/                  # Documentation content
│   └── src/                   # Custom CSS and components
├── scripts/                   # Convenience scripts
│   ├── start.sh
│   ├── stop.sh
│   ├── restart.sh
│   ├── logs.sh
│   └── run-all-seeds.sh
├── .github/workflows/         # GitHub Actions workflows
├── CONTRIBUTING.md            # This file
├── LICENSE                    # MIT License
├── README.md                  # Project README
├── Dockerfile                 # Docker build configuration
├── docker-compose.yml         # Docker Compose configuration
└── pom.xml                    # Maven parent POM
```

## Community

### Getting Help

- **Documentation**: Check [docs](https://dream-horizon-org.github.io/raven-thunder/) first
- **Discussions**: Ask on [GitHub Discussions](https://github.com/dream-horizon-org/thunder/discussions)
- **Issues**: Search [existing issues](https://github.com/dream-horizon-org/thunder/issues)

### Communication Channels

- **GitHub Discussions**: General questions and discussions
- **GitHub Issues**: Bug reports and feature requests
- **Pull Requests**: Code contributions

### Recognition

Contributors will be:

- Listed in CONTRIBUTORS.md (if created)
- Mentioned in release notes
- Credited in documentation

## Questions?

If you have questions about contributing:

1. Check the [documentation](https://dream-horizon-org.github.io/raven-thunder/)
2. Search [existing issues](https://github.com/dream-horizon-org/thunder/issues)
3. Ask on [GitHub Discussions](https://github.com/dream-horizon-org/thunder/discussions)
4. Contact maintainers (if needed)

## License

By contributing, you agree that your contributions will be licensed under the [MIT License](./LICENSE).

---

Thank you for contributing to Thunder! 🎉

