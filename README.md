# 🔌 API Testing Suite — Rest Assured & Postman

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![RestAssured](https://img.shields.io/badge/Rest_Assured-009639?style=for-the-badge)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)

> Comprehensive API test suite using Rest Assured and Postman for validating FinTech data feeds, third-party integrations, and AI endpoints in GRC platforms.

---

## 📌 About This Project

Built for **360factors Predict360 GRC platform** — validates all REST APIs including risk data feeds, compliance monitoring endpoints, regulatory change webhooks, and AI-powered integration services.

### Key Achievements
- ✅ Full API coverage for Predict360 data feeds and third-party integrations
- ✅ AI endpoint validation for Ask Kaia compliance assistant
- ✅ Automated regression via Jenkins CI/CD pipeline
- ✅ Postman collections used for exploratory and smoke testing

---

## 🏗️ Framework Structure

```
api-testing-rest-assured/
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   ├── endpoints/
│   │   │   ├── payload/
│   │   │   ├── tests/
│   │   │   │   ├── RiskManagementAPITest.java
│   │   │   │   ├── ComplianceAPITest.java
│   │   │   │   ├── AuditAPITest.java
│   │   │   │   └── AIEndpointTest.java
│   │   │   └── utils/
│   │   └── resources/
│   │       └── testng.xml
├── postman/
│   ├── Predict360_API_Collection.json
│   └── Predict360_Environment.json
└── pom.xml
```

---

## 🛠️ Tech Stack

| Tool | Purpose |
|---|---|
| Rest Assured | API Test Automation |
| Postman | Exploratory & Smoke Testing |
| Java | Scripting Language |
| TestNG | Test Runner |
| Maven | Build Tool |
| Jenkins | CI/CD Pipeline |

---

## 🚀 API Modules Tested

- **Risk Management APIs** — RCSA data feeds, risk scoring endpoints
- **Compliance Monitoring APIs** — Regulatory change webhooks, alerts
- **Policy Management APIs** — Document CRUD, version control
- **Internal Audit APIs** — Audit trail, findings management
- **TPRM APIs** — Third-party vendor risk integrations
- **AI Endpoints** — Ask Kaia regulatory Q&A, citation mapping

---

## 📊 Test Coverage

| Module | Endpoints | Coverage |
|---|---|---|
| Risk Management | 25+ | 100% |
| Compliance | 30+ | 95% |
| Internal Audit | 20+ | 100% |
| AI Endpoints | 15+ | 90% |

---

## 👨‍💻 Author

**Muhammad Ammar Ahmed** — Senior Test Automation Engineer
📧 m.ammarahmed97@gmail.com
🔗 [LinkedIn](https://linkedin.com/in/ammarahmed) | [GitHub](https://github.com/AmmarAhmed9797)
