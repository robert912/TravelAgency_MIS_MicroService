package com.travel.app.controllers;

import com.travel.app.entities.RestrictionEntity;
import com.travel.app.services.RestrictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restrictions")
@CrossOrigin("*")
public class RestrictionController {

    @Autowired
    RestrictionService restrictionService;

    @GetMapping("/")
    public ResponseEntity<List<RestrictionEntity>> listRestrictions() {
        List<RestrictionEntity> restrictions = restrictionService.getRestrictions();
        return ResponseEntity.ok(restrictions);
    }

    @GetMapping("/active")
    public ResponseEntity<List<RestrictionEntity>> listRestrictionsActive() {
        List<RestrictionEntity> restrictions = restrictionService.getRestrictionsActive();
        return ResponseEntity.ok(restrictions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestrictionEntity> getRestrictionById(@PathVariable Long id) {
        RestrictionEntity restriction = restrictionService.getRestrictionById(id);
        return restriction != null ? ResponseEntity.ok(restriction) : ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<RestrictionEntity> saveRestriction(@RequestBody RestrictionEntity restriction) {
        RestrictionEntity newRestriction = restrictionService.saveRestriction(restriction);
        return ResponseEntity.ok(newRestriction);
    }

    @PutMapping("/")
    public ResponseEntity<RestrictionEntity> updateRestriction(@RequestBody RestrictionEntity restriction) {
        RestrictionEntity updatedRestriction = restrictionService.updateRestriction(restriction);
        return ResponseEntity.ok(updatedRestriction);
    }

}