package com.travel.app.controllers;

import com.travel.app.entities.TourPackageRestrictionEntity;
import com.travel.app.services.TourPackageRestrictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tour-package-restrictions")
@CrossOrigin("*")
public class TourPackageRestrictionController {

    @Autowired
    private TourPackageRestrictionService tourPackageRestrictionService;

    // Endpoint para sincronizar restricciones
    @PutMapping("/package/{packageId}/sync")
    public ResponseEntity<Map<String, Boolean>> syncRestrictions(
            @PathVariable Long packageId,
            @RequestBody Map<String, List<Long>> request,
            @RequestParam(defaultValue = "1") Long userId) {

        List<Long> restrictionIds = request.get("restrictionIds");
        if (restrictionIds == null) {
            return ResponseEntity.badRequest().build();
        }

        tourPackageRestrictionService.syncPackageRestrictions(packageId, restrictionIds, userId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}