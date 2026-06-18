package com.travel.app.controllers;

import com.travel.app.entities.TravelTypeEntity;
import com.travel.app.services.TravelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-types")
public class TravelTypeController {

    @Autowired
    TravelTypeService travelTypeService;

    @GetMapping("/")
    public ResponseEntity<List<TravelTypeEntity>> listTravelTypes() {
        return ResponseEntity.ok(travelTypeService.getTravelTypes());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TravelTypeEntity>> listTravelTypesActive() {
        return ResponseEntity.ok(travelTypeService.getTravelTypesActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelTypeEntity> getById(@PathVariable Long id) {
        TravelTypeEntity travelType = travelTypeService.getTravelTypeById(id);
        return travelType != null ? ResponseEntity.ok(travelType) : ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<TravelTypeEntity> save(@RequestBody TravelTypeEntity travelType) {
        return ResponseEntity.ok(travelTypeService.saveTravelType(travelType));
    }

    @PutMapping("/")
    public ResponseEntity<TravelTypeEntity> update(@RequestBody TravelTypeEntity travelType) {
        return ResponseEntity.ok(travelTypeService.updateTravelType(travelType));
    }
}
