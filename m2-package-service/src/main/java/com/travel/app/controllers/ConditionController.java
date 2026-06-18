package com.travel.app.controllers;

import com.travel.app.entities.ConditionEntity;
import com.travel.app.services.ConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conditions")
public class ConditionController {

    @Autowired
    ConditionService conditionService;

    @GetMapping("/")
    public ResponseEntity<List<ConditionEntity>> listConditions() {
        List<ConditionEntity> conditions = conditionService.getConditions();
        return ResponseEntity.ok(conditions);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ConditionEntity>> listConditionsActive() {
        List<ConditionEntity> conditions = conditionService.getConditionsActive();
        return ResponseEntity.ok(conditions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConditionEntity> getConditionById(@PathVariable Long id) {
        ConditionEntity condition = conditionService.getConditionById(id);
        return condition != null ? ResponseEntity.ok(condition) : ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<ConditionEntity> saveCondition(@RequestBody ConditionEntity condition) {
        ConditionEntity newCondition = conditionService.saveCondition(condition);
        return ResponseEntity.ok(newCondition);
    }

    @PutMapping("/")
    public ResponseEntity<ConditionEntity> updateCondition(@RequestBody ConditionEntity condition) {
        ConditionEntity updatedCondition = conditionService.updateCondition(condition);
        return ResponseEntity.ok(updatedCondition);
    }

}