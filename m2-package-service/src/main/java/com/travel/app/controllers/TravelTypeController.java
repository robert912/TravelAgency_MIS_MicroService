package com.travel.app.controllers;

import com.travel.app.entities.TravelTypeEntity;
import com.travel.app.services.TravelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-types")
@CrossOrigin("*")
public class TravelTypeController {

    @Autowired
    TravelTypeService travelTypeService;

    @GetMapping("/")
    public ResponseEntity<List<TravelTypeEntity>> listTravelTypes() {
        List<TravelTypeEntity> list = travelTypeService.getTravelTypes();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/active")
    public ResponseEntity<List<TravelTypeEntity>> listTravelTypesActive() {
        List<TravelTypeEntity> list = travelTypeService.getTravelTypesActive();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelTypeEntity> getById(@PathVariable Long id) {
        TravelTypeEntity travelType = travelTypeService.getTravelTypeById(id);
        return travelType != null ? ResponseEntity.ok(travelType) : ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<TravelTypeEntity> save(@RequestBody TravelTypeEntity travelType) {
        TravelTypeEntity newTravelType = travelTypeService.saveTravelType(travelType);
        return ResponseEntity.ok(newTravelType);
    }

    @PutMapping("/")
    public ResponseEntity<TravelTypeEntity> update(@RequestBody TravelTypeEntity travelType) {
        TravelTypeEntity updatedTravelType = travelTypeService.updateTravelType(travelType);
        return ResponseEntity.ok(updatedTravelType);
    }
    
}