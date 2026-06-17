package com.travel.app.controllers;

import com.travel.app.entities.TourPackageServiceEntity;
import com.travel.app.services.TourPackageServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tour-package-services")
@CrossOrigin("*")
public class TourPackageServiceController {

    @Autowired
    private TourPackageServiceService tourPackageServiceService;

    // Endpoint para sincronizar servicios
    @PutMapping("/package/{packageId}/sync")
    public ResponseEntity<Map<String, Boolean>> syncServices(
            @PathVariable Long packageId,
            @RequestBody Map<String, List<Long>> request,
            @RequestParam(defaultValue = "1") Long userId) {

        List<Long> serviceIds = request.get("serviceIds");
        if (serviceIds == null) {
            return ResponseEntity.badRequest().build();
        }

        tourPackageServiceService.syncPackageServices(packageId, serviceIds, userId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}