package com.example.gamify.controllers;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
@RequestMapping("/health")
public class HealthController {

    private static final String UP = "UP";
    private static final String DOWN = "DOWN";
    private static final int DB_TIMEOUT_SECONDS = 2;

    // Controllers that must be registered for the API to be considered healthy
    private static final Set<String> REQUIRED_CONTROLLERS = Set.of(
            "TasksController", "MissionController", "ProfileController");

    private final DataSource dataSource;
    private final RequestMappingHandlerMapping handlerMapping;

    public HealthController(DataSource dataSource, @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.dataSource = dataSource;
        this.handlerMapping = handlerMapping;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> database = checkDatabase();
        Map<String, Object> endpoints = checkEndpoints();
        boolean up = UP.equals(database.get("status")) && UP.equals(endpoints.get("status"));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", up ? UP : DOWN);
        body.put("timestamp", Instant.now().toString());
        body.put("uptimeSeconds", ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        Map<String, Object> components = new LinkedHashMap<>();
        components.put("database", database);
        components.put("endpoints", endpoints);
        body.put("components", components);

        return ResponseEntity.status(up ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    private Map<String, Object> checkDatabase() {
        Map<String, Object> result = new LinkedHashMap<>();
        long start = System.nanoTime();
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(DB_TIMEOUT_SECONDS);
            result.put("status", valid ? UP : DOWN);
            result.put("responseTimeMs", (System.nanoTime() - start) / 1_000_000);
        } catch (Exception e) {
            result.put("status", DOWN);
            result.put("error", e.getClass().getSimpleName());
        }
        return result;
    }

    private Map<String, Object> checkEndpoints() {
        Map<String, Object> result = new LinkedHashMap<>();
        Set<String> registered = new TreeSet<>();
        int total = 0;
        for (var handler : handlerMapping.getHandlerMethods().values()) {
            Class<?> type = handler.getBeanType();
            if (type.getPackageName().startsWith("com.example.gamify") && !type.equals(HealthController.class)) {
                registered.add(type.getSimpleName());
                total++;
            }
        }
        Set<String> missing = new TreeSet<>(REQUIRED_CONTROLLERS);
        missing.removeAll(registered);

        result.put("status", missing.isEmpty() ? UP : DOWN);
        result.put("registeredEndpoints", total);
        result.put("controllers", registered);
        if (!missing.isEmpty()) {
            result.put("missingControllers", missing);
        }
        return result;
    }
}
