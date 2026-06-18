package com.travel.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tour-packages")
public class SearchController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/active")
    public ResponseEntity<?> listTourPackagesActive() {
        try {
            List<?> packages = restTemplate.exchange(
                    "http://m2-package-service/api/tour-packages/active",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {}
            ).getBody();
            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error fetching active packages: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPackages(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long travelTypeId
    ) {
        StringBuilder urlBuilder = new StringBuilder("http://m2-package-service/api/tour-packages/search?");
        if (destination != null) urlBuilder.append("destination=").append(destination).append("&");
        if (minPrice != null) urlBuilder.append("minPrice=").append(minPrice).append("&");
        if (maxPrice != null) urlBuilder.append("maxPrice=").append(maxPrice).append("&");
        if (startDate != null) urlBuilder.append("startDate=").append(startDate).append("&");
        if (endDate != null) urlBuilder.append("endDate=").append(endDate).append("&");
        if (travelTypeId != null) urlBuilder.append("travelTypeId=").append(travelTypeId).append("&");

        try {
            List<?> packages = restTemplate.exchange(
                    urlBuilder.toString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {}
            ).getBody();
            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error searching packages: " + e.getMessage()));
        }
    }

    @GetMapping("/{packageId}/availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(@PathVariable Long packageId) {
        Map<String, Object> pkg;
        try {
            pkg = restTemplate.getForObject("http://m2-package-service/api/tour-packages/" + packageId, Map.class);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        if (pkg == null) {
            return ResponseEntity.notFound().build();
        }

        int totalSlots = pkg.get("totalSlots") != null ? (Integer) pkg.get("totalSlots") : 0;
        
        Integer reservedSlots;
        try {
            reservedSlots = restTemplate.getForObject(
                "http://m4-reservation-service/api/reservations/count-confirmed-passengers?packageId=" + packageId,
                Integer.class
            );
        } catch (Exception e) {
            reservedSlots = 0;
        }
        if (reservedSlots == null) reservedSlots = 0;

        int availableSlots = totalSlots - reservedSlots;

        Map<String, Object> response = new HashMap<>();
        response.put("totalSlots", totalSlots);
        response.put("reservedSlots", reservedSlots);
        response.put("availableSlots", availableSlots);
        response.put("isAvailable", availableSlots > 0);
        response.put("packageName", pkg.get("name"));
        response.put("destination", pkg.get("destination"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{packageId}/availability/check")
    public ResponseEntity<Map<String, Object>> checkAvailabilityForQuantity(
            @PathVariable Long packageId,
            @RequestParam int quantity) {

        Map<String, Object> pkg;
        try {
            pkg = restTemplate.getForObject("http://m2-package-service/api/tour-packages/" + packageId, Map.class);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        if (pkg == null) {
            return ResponseEntity.notFound().build();
        }

        int totalSlots = pkg.get("totalSlots") != null ? (Integer) pkg.get("totalSlots") : 0;
        
        Integer reservedSlots;
        try {
            reservedSlots = restTemplate.getForObject(
                "http://m4-reservation-service/api/reservations/count-confirmed-passengers?packageId=" + packageId,
                Integer.class
            );
        } catch (Exception e) {
            reservedSlots = 0;
        }
        if (reservedSlots == null) reservedSlots = 0;

        int availableSlots = totalSlots - reservedSlots;

        Map<String, Object> response = new HashMap<>();
        response.put("totalSlots", totalSlots);
        response.put("reservedSlots", reservedSlots);
        response.put("availableSlots", availableSlots);
        response.put("requestedQuantity", quantity);
        response.put("isAvailable", availableSlots >= quantity);
        response.put("message", availableSlots >= quantity ?
                "Hay suficientes cupos disponibles" :
                String.format("Solo hay %d cupos disponibles de %d solicitados", availableSlots, quantity));

        return ResponseEntity.ok(response);
    }
}
