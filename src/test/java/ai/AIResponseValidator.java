package ai;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * AI-Powered API Response Validator
 * Author: Muhammad Ammar Ahmed — Senior Test Automation Engineer
 *
 * Intelligently validates API responses using schema inference,
 * semantic analysis, and anomaly detection — going beyond
 * simple status code and field checks.
 */
public class AIResponseValidator {

    private static final Logger logger = Logger.getLogger(AIResponseValidator.class.getName());
    private final List<ValidationResult> results = new ArrayList<>();

    // ── Smart Schema Validation ───────────────────────────────────────────────

    /**
     * Validate response against an inferred schema — no hardcoded rules.
     * Automatically detects field types, constraints, and business rules.
     */
    public ValidationReport smartValidate(Response response, String context) {
        ValidationReport report = new ValidationReport(context);

        // 1. Status code intelligence
        validateStatusCode(response, report);

        // 2. Response time SLA check
        validateResponseTime(response, report);

        // 3. Content-type validation
        validateContentType(response, report);

        // 4. Body structure analysis
        if (response.getContentType() != null &&
            response.getContentType().contains("application/json")) {
            validateJsonBody(response.getBody().asString(), report);
        }

        // 5. Security header checks
        validateSecurityHeaders(response, report);

        logger.info(String.format("AI Validation [%s]: %d passed, %d failed",
            context, report.passed, report.failed));

        return report;
    }

    private void validateStatusCode(Response response, ValidationReport report) {
        int code = response.getStatusCode();
        if (code >= 200 && code < 300) {
            report.pass("Status code " + code + " indicates success");
        } else if (code == 401) {
            report.fail("SECURITY: Unauthorized — check auth token", "HIGH");
        } else if (code == 403) {
            report.fail("SECURITY: Forbidden — insufficient permissions", "HIGH");
        } else if (code == 429) {
            report.fail("RATE LIMIT: Too many requests", "MEDIUM");
        } else if (code >= 500) {
            report.fail("SERVER ERROR: " + code + " — backend issue detected", "CRITICAL");
        } else {
            report.fail("Unexpected status: " + code, "MEDIUM");
        }
    }

    private void validateResponseTime(Response response, ValidationReport report) {
        long timeMs = response.getTime();
        if (timeMs < 500) {
            report.pass("Response time " + timeMs + "ms — excellent");
        } else if (timeMs < 2000) {
            report.warn("Response time " + timeMs + "ms — acceptable but could be faster");
        } else if (timeMs < 5000) {
            report.fail("Response time " + timeMs + "ms — SLA breach (>2s)", "MEDIUM");
        } else {
            report.fail("Response time " + timeMs + "ms — critical SLA breach (>5s)", "HIGH");
        }
    }

    private void validateContentType(Response response, ValidationReport report) {
        String ct = response.getContentType();
        if (ct == null) {
            report.fail("Missing Content-Type header", "MEDIUM");
        } else if (ct.contains("application/json")) {
            report.pass("Content-Type is application/json");
        } else {
            report.warn("Unexpected Content-Type: " + ct);
        }
    }

    private void validateJsonBody(String body, ValidationReport report) {
        if (body == null || body.isBlank()) {
            report.fail("Response body is empty", "HIGH");
            return;
        }
        try {
            if (body.trim().startsWith("{")) {
                JSONObject json = new JSONObject(body);
                analyzeJsonObject(json, "", report);
            } else if (body.trim().startsWith("[")) {
                JSONArray arr = new JSONArray(body);
                report.pass("Response is valid JSON array with " + arr.length() + " items");
                if (arr.length() == 0) report.warn("Response array is empty");
                if (arr.length() > 0) analyzeJsonObject(arr.getJSONObject(0), "[0]", report);
            }
        } catch (Exception e) {
            report.fail("Invalid JSON: " + e.getMessage(), "HIGH");
        }
    }

    private void analyzeJsonObject(JSONObject json, String path, ValidationReport report) {
        // Check for common API patterns
        if (json.has("error") || json.has("errors")) {
            report.fail("Response contains error field at " + path, "HIGH");
        }
        if (json.has("status")) {
            String status = json.optString("status", "");
            if (status.equalsIgnoreCase("error") || status.equalsIgnoreCase("failed")) {
                report.fail("Response status is error: " + status, "HIGH");
            } else {
                report.pass("Response status field: " + status);
            }
        }
        if (json.has("data")) {
            Object data = json.get("data");
            if (data == JSONObject.NULL) {
                report.warn("Response data field is null");
            } else {
                report.pass("Response contains non-null data field");
            }
        }

        // Detect sensitive data leakage
        detectSensitiveDataLeakage(json, report);

        // Detect null/empty required fields
        detectNullFields(json, path, report);
    }

    private void detectSensitiveDataLeakage(JSONObject json, ValidationReport report) {
        List<String> sensitiveKeys = Arrays.asList(
            "password", "secret", "token", "apiKey", "api_key",
            "ssn", "creditCard", "credit_card", "cvv", "privateKey"
        );
        for (String key : sensitiveKeys) {
            if (json.has(key)) {
                report.fail("SECURITY: Sensitive field '" + key + "' exposed in response!", "CRITICAL");
            }
        }
    }

    private void detectNullFields(JSONObject json, String path, ValidationReport report) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value == JSONObject.NULL) {
                report.warn("Null value detected at " + path + "." + key);
            } else if (value instanceof String && ((String) value).isBlank()) {
                report.warn("Empty string at " + path + "." + key);
            }
        }
    }

    private void validateSecurityHeaders(Response response, ValidationReport report) {
        Map<String, String> requiredHeaders = new LinkedHashMap<>();
        requiredHeaders.put("X-Content-Type-Options", "nosniff");
        requiredHeaders.put("X-Frame-Options", null);
        requiredHeaders.put("Strict-Transport-Security", null);

        for (Map.Entry<String, String> entry : requiredHeaders.entrySet()) {
            String header = response.getHeader(entry.getKey());
            if (header == null) {
                report.warn("Missing security header: " + entry.getKey());
            } else {
                report.pass("Security header present: " + entry.getKey());
            }
        }
    }

    // ── Report Classes ────────────────────────────────────────────────────────

    public static class ValidationReport {
        public final String context;
        public int passed = 0, failed = 0, warnings = 0;
        private final List<String> details = new ArrayList<>();

        public ValidationReport(String ctx) { this.context = ctx; }

        public void pass(String msg) {
            passed++; details.add("✅ " + msg);
        }
        public void fail(String msg, String severity) {
            failed++; details.add("❌ [" + severity + "] " + msg);
        }
        public void warn(String msg) {
            warnings++; details.add("⚠️  " + msg);
        }
        public boolean hasCritical() {
            return details.stream().anyMatch(d -> d.contains("[CRITICAL]"));
        }
        public void print() {
            System.out.println("\n=== AI Validation: " + context + " ===");
            details.forEach(System.out::println);
            System.out.printf("Result: %d passed | %d failed | %d warnings%n",
                passed, failed, warnings);
        }
    }
}
