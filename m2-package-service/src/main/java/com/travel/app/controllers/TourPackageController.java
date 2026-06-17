package com.travel.app.controllers;

import com.travel.app.entities.TourPackageEntity;
import com.travel.app.services.TourPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tour-packages")
@CrossOrigin("*")
public class TourPackageController {

    @Autowired
    private TourPackageService tourPackageService;

    @GetMapping("/")
    public ResponseEntity<List<TourPackageEntity>> listTourPackages() {
        List<TourPackageEntity> tourPackages = tourPackageService.getTourPackages();
        return ResponseEntity.ok(tourPackages);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TourPackageEntity>> listTourPackagesActive() {
        List<TourPackageEntity> tourPackages = tourPackageService.getTourPackagesActive();
        return ResponseEntity.ok(tourPackages);
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
        List<TourPackageEntity> filteredPackages = tourPackageService.filterTourPackages(
                destination, minPrice, maxPrice, startDate, endDate, travelTypeId);

        return ResponseEntity.ok(filteredPackages);
    }

    @PostMapping("/")
    public ResponseEntity<TourPackageEntity> saveTourPackage(@RequestBody TourPackageEntity tourPackage) {
        TourPackageEntity newTourPackage = tourPackageService.saveTourPackage(tourPackage);
        return ResponseEntity.ok(newTourPackage);
    }

    @PutMapping("/")
    public ResponseEntity<TourPackageEntity> updateTourPackage(@RequestBody TourPackageEntity tourPackage) {
        TourPackageEntity updatedTourPackage = tourPackageService.updateTourPackage(tourPackage);
        return ResponseEntity.ok(updatedTourPackage);
    }
}
