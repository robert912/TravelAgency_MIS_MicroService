package com.travel.app.controllers;

import com.travel.app.entities.TourPackageEntity;
import com.travel.app.services.TourPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tour-packages")
public class TourPackageController {

    @Autowired
    private TourPackageService tourPackageService;

    @GetMapping("/")
    public ResponseEntity<List<TourPackageEntity>> listTourPackages() {
        return ResponseEntity.ok(tourPackageService.getTourPackages());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TourPackageEntity>> listTourPackagesActive() {
        return ResponseEntity.ok(tourPackageService.getTourPackagesActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourPackageEntity> getTourPackageById(@PathVariable Long id) {
        TourPackageEntity tourPackage = tourPackageService.getTourPackageById(id);
        return tourPackage != null ? ResponseEntity.ok(tourPackage) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TourPackageEntity>> searchPackages(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long travelTypeId
    ) {
        return ResponseEntity.ok(tourPackageService.filterTourPackages(
                destination, minPrice, maxPrice, startDate, endDate, travelTypeId));
    }

    @PostMapping("/")
    public ResponseEntity<TourPackageEntity> saveTourPackage(@RequestBody TourPackageEntity tourPackage) {
        return ResponseEntity.ok(tourPackageService.saveTourPackage(tourPackage));
    }

    @PutMapping("/")
    public ResponseEntity<TourPackageEntity> updateTourPackage(@RequestBody TourPackageEntity tourPackage) {
        return ResponseEntity.ok(tourPackageService.updateTourPackage(tourPackage));
    }
}
