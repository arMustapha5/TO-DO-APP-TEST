# Seletest - Todo Application Test Automation

## Prerequisites
- Docker + Docker Compose

## Setup & Run

### 1. Start Applications
```bash
docker-compose up -d
```
- Frontend: http://localhost:3000
- Backend: http://localhost:3001

### 2. Run API Tests
```bash
cd todo-api-test
./mvnw test
```

### 3. Run Selenium Tests
```bash
cd todo-ui-test
./mvnw test
```

Add `-Dheadless` flag to run in head mode:
```bash
./mvnw test -Dheadless=false
```

### 4. Generate Allure Reports
After running tests, generate reports:
```bash
./mvnw allure:serve
```

### 5. Stop Applications
```bash
docker-compose down
```

## Demo Credentials
- Admin: `admin` / `password`
- User: `user` / `123456`

---

For detailed test strategy, see [test_strategy.md](test_strategy.md)
