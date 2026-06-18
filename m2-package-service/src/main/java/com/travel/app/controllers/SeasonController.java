package com.travel.app.controllers;

import com.travel.app.entities.SeasonEntity;
import com.travel.app.services.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
public class SeasonController {

    @Autowired
    SeasonService seasonService;

    @GetMapping("/")
    public ResponseEntity<List<SeasonEntity>> listSeasons() {
        List<SeasonEntity> seasons = seasonService.getSeasons();
        return ResponseEntity.ok(seasons);
    }

    @GetMapping("/active")
    public ResponseEntity<List<SeasonEntity>> listSeasonsActive() {
        List<SeasonEntity> seasons = seasonService.getSeasonsActive();
        return ResponseEntity.ok(seasons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeasonEntity> getSeasonById(@PathVariable Long id) {
        SeasonEntity season = seasonService.getSeasonById(id);
        return season != null ? ResponseEntity.ok(season) : ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<SeasonEntity> saveSeason(@RequestBody SeasonEntity season) {
        SeasonEntity newSeason = seasonService.saveSeason(season);
        return ResponseEntity.ok(newSeason);
    }

    @PutMapping("/")
    public ResponseEntity<SeasonEntity> updateSeason(@RequestBody SeasonEntity season) {
        SeasonEntity updatedSeason = seasonService.updateSeason(season);
        return ResponseEntity.ok(updatedSeason);
    }

}