# Test Strategy and Plan

## Overview

This document outlines the comprehensive test strategy for the Todo Application automated testing suite, covering both UI and API test automation using Selenium WebDriver and REST Assured frameworks.

## Application Under Test

**Todo Application** - A full-stack web application consisting of:
- **Frontend**: React TypeScript application (localhost:3000)
- **Backend**: Node.js Express API (localhost:3001)
- **Authentication**: JWT token-based authentication
- **Data Storage**: In-memory storage (suitable for testing)

## Test Objectives

1. **Functional Validation**: Ensure all core features work as expected
2. **Integration Testing**: Verify frontend-backend communication
3. **Authentication Testing**: Validate security mechanisms
4. **Error Handling**: Test negative scenarios and edge cases
5. **User Experience**: Ensure seamless user workflows

## Test Scope

### In Scope
- ✅ User authentication (login/logout)
- ✅ Todo CRUD operations (Create, Read, Update, Delete)
- ✅ API endpoint functionality
- ✅ Frontend user interactions
- ✅ Error handling and validation
- ✅ Cross-user data isolation

### Out of Scope
- ❌ Performance testing
- ❌ Security penetration testing
- ❌ Cross-browser compatibility (Chrome only)
- ❌ Mobile responsiveness
- ❌ Database persistence testing

## Testing Approach

### 1. UI Automation Testing
**Framework**: Selenium WebDriver + JUnit 5
**Browser**: Chrome (headless mode)
**Pattern**: Page Object Model (POM)

**Test Coverage**:
- Login functionality with valid/invalid credentials
- Todo creation and verification
- Todo editing and updates
- Todo deletion
- Complete CRUD workflows
- User session management

**Test Scenarios**:
- **Positive Cases**: Valid inputs, successful operations
- **Negative Cases**: Invalid credentials, unauthorized access, empty inputs
- **Edge Cases**: Special characters, long text, concurrent operations

### 2. API Automation Testing
**Framework**: REST Assured + JUnit 5
**Approach**: Direct API calls with JSON payload validation

**Test Coverage**:
- Authentication endpoint (/login)
- Todo management endpoints (/items)
- Authorization validation
- Error response validation
- Data isolation between users

**API Endpoints Tested**:
- `POST /login` - Authentication with valid/invalid credentials
- `GET /items` - Retrieve todos with proper authorization
- `POST /items` - Create new todos with validation
- `PUT /items/:id` - Update existing todos
- `DELETE /items/:id` - Delete todos with proper checks

## Test Environment

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Docker + Docker Compose
- Chrome browser (for UI tests)

### Application Startup
Applications are containerized using Docker:
1. Backend API: Docker container (Port 3001)
2. Frontend React: Docker container (Port 3000)
3. Health checks ensure services are ready before tests

## Test Data

### Test Users
- **Admin User**: username=`admin`, password=`password`
- **Regular User**: username=`user`, password=`123456`

### Test Data Management
- Tests use isolated user accounts
- Data resets between test runs
- In-memory storage (no persistence)
- Cross-user data isolation validated

## Test Execution

### UI Tests
```bash
# Headless execution (CI)
mvn clean test -Dheadless -f todo-ui-test/pom.xml

# GUI execution (debugging)
mvn clean test -Dgui -f todo-ui-test/pom.xml
```

### API Tests
```bash
# API test execution
mvn clean test -f todo-api-test/pom.xml
```

### Combined Execution
```bash
# Using Docker Compose
docker-compose up -d
cd todo-api-test && mvn clean test
cd ../todo-ui-test && mvn clean test -Dheadless
docker-compose down
```

## Test Reporting

- **JUnit Reports**: Generated in `target/surefire-reports/`
- **Console Output**: Real-time test execution status
- **HTML Reports**: Detailed test results with pass/fail status
- **CI Integration**: Automated reporting in GitHub Actions

## Tools and Frameworks

| Component | Tool/Framework | Version | Purpose |
|-----------|---------------|---------|---------|
| UI Testing | Selenium WebDriver | 4.26.0 | Browser automation |
| API Testing | REST Assured | 5.3.2 | API testing |
| Test Framework | JUnit 5 | 5.10.1 | Test execution |
| Build Tool | Maven | 3.x | Dependency management |
| WebDriver Management | WebDriverManager | 5.9.2 | Automatic driver setup |
| Containerization | Docker Compose | 3.8 | Application orchestration |

## Assumptions and Limitations

### Assumptions
- Application runs on standard ports (3000/3001)
- Test data is reset between test runs
- Network connectivity is stable
- Chrome browser is available for UI tests
- Docker environment is properly configured

### Limitations
- Tests run in headless mode only (in CI)
- No parallel test execution
- In-memory data storage (no persistence)
- Limited to localhost environment
- No visual regression testing
- Chrome browser only for UI tests

## Test Maintenance

### Best Practices
- Use Page Object Model for UI tests
- Implement proper wait strategies
- Maintain clean test data
- Use descriptive test names
- Implement proper error handling
- Regular test review and updates

### Code Structure
```
UI Tests (todo-ui-test/):
├── src/test/java/com/seletest/
│   ├── pages/          # Page Object classes
│   ├── tests/          # Test classes
│   └── utils/          # Utility classes

API Tests (todo-api-test/):
├── src/test/java/com/seletest/api/
│   ├── tests/          # Test classes
│   ├── models/         # Data models
│   └── utils/          # Helper utilities
```

## Success Criteria

### Pass Criteria
- ✅ All authentication scenarios pass
- ✅ Complete CRUD operations work
- ✅ Error handling validates correctly
- ✅ User data isolation maintained
- ✅ All API endpoints respond correctly

### Metrics
- **Test Coverage**: 100% of defined scenarios
- **Pass Rate**: All tests must pass
- **Execution Time**: Under 5 minutes total
- **Setup Time**: Under 2 minutes

## Continuous Integration

The test suite is integrated with GitHub Actions:
- Automated execution on push/PR
- Docker-based application startup
- Parallel test execution (API → UI)
- Automatic cleanup and reporting
- Artifact collection for test reports

---

*This test strategy ensures comprehensive coverage of the Todo Application while maintaining efficiency and reliability in the test automation suite.*
