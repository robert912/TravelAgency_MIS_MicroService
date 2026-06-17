package com.travel.app.services;

import com.travel.app.entities.SeasonEntity;
import com.travel.app.repositories.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonService {

    @Autowired
    SeasonRepository seasonRepository;

    // Listar todas las temporadas de viaje
    public List<SeasonEntity> getSeasons() {
        return seasonRepository.findAll();
    }

    // Listar solo temporadas activas
    public List<SeasonEntity> getSeasonsActive() {
        return seasonRepository.findByActive(1);
    }

    // Guardar nueva temporada
    public SeasonEntity saveSeason(SeasonEntity season) {
        return seasonRepository.save(season);
    }

    // Buscar por ID (solo si está activa)
    public SeasonEntity getSeasonById(Long id) {
        SeasonEntity season = seasonRepository.findById(id).orElse(null);
        if (season != null /*&& season.getActive() == 1*/) {
            return season;
        }
        return null;
    }

    // Actualizar temporada
    public SeasonEntity updateSeason(SeasonEntity season) {
        return seasonRepository.save(season);
    }

    // Borrado lógico (Desactivar)
    public boolean deleteSeason(Long id) throws Exception {
        try {
            SeasonEntity season = seasonRepository.findById(id).orElse(null);
            if (season != null) {
                season.setActive(0);
                seasonRepository.save(season);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al desactivar la temporada: " + e.getMessage());
        }
    }
}