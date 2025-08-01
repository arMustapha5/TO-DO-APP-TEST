# Test Plan - Todo Application Automation Suite

## Overview

This test plan outlines the automated testing strategy for the Todo Application, a full-stack web application with React frontend and Node.js backend. The testing approach covers both UI automation using Selenium WebDriver and API testing using REST Assured.

## What is Being Tested

### Application Under Test
- **Frontend**: React TypeScript application (Port 3000)
- **Backend**: Node.js Express API (Port 3001)
- **Authentication**: JWT token-based authentication system
- **Data Storage**: In-memory storage for testing isolation

### Core Features Tested
1. **User Authentication**
   - Login with valid credentials
   - Login failure with invalid credentials
   - Session management and logout

2. **Todo Management (CRUD Operations)**
   - Create new todo items
   - Read/retrieve todo lists
   - Update existing todos
   - Delete todo items

3. **Data Isolation**
   - User-specific todo lists
   - Cross-user data separation
   - Authorization validation

## Test Coverage Areas

### UI Test Coverage (Selenium WebDriver)
- **Login Functionality**: Valid/invalid credential handling
- **Todo CRUD Workflows**: Complete user interactions
- **Error Handling**: Invalid inputs and edge cases
- **User Experience**: Navigation and interface responsiveness

### API Test Coverage (REST Assured)
- **Authentication Endpoints**: `/login` with various scenarios
- **Todo Management APIs**: `/items` CRUD operations
- **Authorization Validation**: Token-based access control
- **Error Response Validation**: HTTP status codes and error messages

### Test Scenarios Distribution
- **Positive Test Cases**: 60% - Valid inputs and successful operations
- **Negative Test Cases**: 25% - Invalid data and error conditions
- **Edge Cases**: 15% - Boundary values and special characters

## Tools Used and Rationale

| Tool/Framework | Purpose | Version | Why Chosen |
|---------------|---------|---------|------------|
| **Selenium WebDriver** | UI Automation | 4.26.0 | Industry standard for web automation |
| **REST Assured** | API Testing | 5.3.2 | Excellent Java-based API testing framework |
| **JUnit 5** | Test Framework | 5.10.1 | Modern testing framework with advanced features |
| **Maven** | Build Management | 3.x | Dependency management and test execution |
| **WebDriverManager** | Driver Management | 5.9.2 | Automatic browser driver setup |
| **Docker Compose** | Environment Setup | 3.8 | Consistent application deployment |

### Framework Design Patterns
- **Page Object Model (POM)**: For maintainable UI test code
- **Builder Pattern**: For test data creation
- **Factory Pattern**: For WebDriver initialization

## How to Run the Tests

### Prerequisites
```bash
# Required software
- Java 11 or higher
- Maven 3.6+
- Docker & Docker Compose
- Chrome browser (for UI tests)
```

### Environment Setup
```bash
# 1. Start application services
docker-compose up -d

# 2. Verify services are running
curl http://localhost:3001/health  # API health check
curl http://localhost:3000         # Frontend check
```

### Test Execution Commands

#### API Tests
```bash
# Run API tests
cd todo-api-test
mvn clean test

# With specific test class
mvn test -Dtest=TodoApiTest
```

#### UI Tests
```bash
# Run UI tests (headless mode)
cd todo-ui-test
mvn clean test -Dheadless=true

# Run with GUI (for debugging)
mvn clean test -Dgui=true

# Specific test class
mvn test -Dtest=LoginTest
```

#### Combined Test Execution
```bash
# Full test suite execution
./scripts/run-tests.sh

# Manual step-by-step
docker-compose up -d
mvn clean test -f todo-api-test/pom.xml
mvn clean test -f todo-ui-test/pom.xml -Dheadless=true
docker-compose down
```

### Test Data
- **Test Users**: `admin/password`, `user/123456`
- **Test Todos**: Generated dynamically with unique identifiers
- **Data Reset**: Automatic between test runs (in-memory storage)

## Test Reporting

### Report Generation
- **JUnit Reports**: `target/surefire-reports/TEST-*.xml`
- **HTML Reports**: Available in `target/surefire-reports/`
- **Console Output**: Real-time execution status
- **CI Integration**: GitHub Actions with artifact collection

### Test Metrics
- **Execution Time**: < 5 minutes total runtime
- **Success Rate**: 100% pass rate expected
- **Coverage**: 100% of defined test scenarios

## Assumptions and Limitations

### Assumptions
- Application runs on localhost with standard ports (3000/3001)
- Chrome browser is available and compatible
- Docker environment is properly configured
- Network connectivity is stable during test execution
- Test data resets between runs (in-memory storage)

### Limitations
- **Browser Support**: Chrome only (no cross-browser testing)
- **Environment**: Localhost only (no cloud/staging testing)
- **Performance**: No load or performance testing
- **Security**: No penetration or security testing
- **Mobile**: No mobile responsiveness testing
- **Parallel Execution**: Tests run sequentially, not in parallel
- **Visual Testing**: No screenshot comparison or visual regression

### Known Constraints
- In-memory data storage limits persistence testing
- Headless mode only in CI environment
- No database integration testing
- Limited error simulation capabilities

## Continuous Integration

### GitHub Actions Integration
- **Trigger**: On push to main branch and pull requests
- **Workflow**: Docker startup → API tests → UI tests → Cleanup
- **Artifacts**: Test reports and logs preserved
- **Notifications**: Slack/email notifications on failure

### Success Criteria
- All defined test cases must pass
- Test execution completes within time limits
- No flaky or intermittent test failures
- Proper error reporting and logging

---

*This test plan ensures comprehensive automated testing coverage while maintaining efficiency and reliability for the Todo Application test suite.*