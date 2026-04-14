package tests;

// Compliance API Test Suite — Predict360 GRC Platform
// Author: Muhammad Ammar Ahmed

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ConfigReader;
import utils.TokenManager;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ComplianceAPITest {

    private static String authToken;
    private static String baseUrl;

    @BeforeClass
    public void setup() {
        baseUrl = ConfigReader.get("api.base.url");
        RestAssured.baseURI = baseUrl;
        authToken = TokenManager.getToken(
            ConfigReader.get("api.username"),
            ConfigReader.get("api.password")
        );
    }

    @Test(priority = 1, description = "Verify GET all compliance alerts returns 200")
    public void testGetAllComplianceAlerts() {
        given()
            .header("Authorization", "Bearer " + authToken)
            .header("Content-Type", "application/json")
        .when()
            .get("/api/v1/compliance/alerts")
        .then()
            .statusCode(200)
            .body("status", equalTo("success"))
            .body("data", notNullValue())
            .body("data.size()", greaterThan(0))
            .time(lessThan(3000L));
    }

    @Test(priority = 2, description = "Verify GET compliance alert by ID")
    public void testGetComplianceAlertById() {
        String alertId = "alert_001";
        given()
            .header("Authorization", "Bearer " + authToken)
            .pathParam("id", alertId)
        .when()
            .get("/api/v1/compliance/alerts/{id}")
        .then()
            .statusCode(200)
            .body("data.id", equalTo(alertId))
            .body("data.status", notNullValue())
            .body("data.severity", notNullValue());
    }

    @Test(priority = 3, description = "Verify POST create new compliance alert")
    public void testCreateComplianceAlert() {
        String requestBody = """
            {
                "title": "New Compliance Alert",
                "description": "Test alert created via API",
                "severity": "High",
                "dueDate": "2025-12-31",
                "assignedTo": "user@360factors.com",
                "regulation": "FFIEC"
            }
            """;
        Response response =
        given()
            .header("Authorization", "Bearer " + authToken)
            .header("Content-Type", "application/json")
            .body(requestBody)
        .when()
            .post("/api/v1/compliance/alerts")
        .then()
            .statusCode(201)
            .body("data.title", equalTo("New Compliance Alert"))
            .body("data.severity", equalTo("High"))
            .extract().response();

        String createdId = response.jsonPath().getString("data.id");
        Assert.assertNotNull(createdId, "Created alert ID should not be null");
    }

    @Test(priority = 4, description = "Verify PUT update alert status")
    public void testUpdateAlertStatus() {
        String alertId = "alert_001";
        String requestBody = "{\"status\": \"Resolved\"}";
        given()
            .header("Authorization", "Bearer " + authToken)
            .header("Content-Type", "application/json")
            .pathParam("id", alertId)
            .body(requestBody)
        .when()
            .put("/api/v1/compliance/alerts/{id}")
        .then()
            .statusCode(200)
            .body("data.status", equalTo("Resolved"));
    }

    @Test(priority = 5, description = "Verify DELETE compliance alert")
    public void testDeleteComplianceAlert() {
        String alertId = "alert_temp_001";
        given()
            .header("Authorization", "Bearer " + authToken)
            .pathParam("id", alertId)
        .when()
            .delete("/api/v1/compliance/alerts/{id}")
        .then()
            .statusCode(204);
    }

    @Test(priority = 6, description = "Verify unauthorized access returns 401")
    public void testUnauthorizedAccess() {
        given()
            .header("Authorization", "Bearer invalid_token")
        .when()
            .get("/api/v1/compliance/alerts")
        .then()
            .statusCode(401)
            .body("error", equalTo("Unauthorized"));
    }

    @AfterClass
    public void teardown() {
        TokenManager.invalidateToken(authToken);
    }
  }
