package com.travel.app.services;

import com.travel.app.entities.RestrictionEntity;
import com.travel.app.repositories.RestrictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestrictionService {

    @Autowired
    RestrictionRepository restrictionRepository;

    // Obtener todas las restricciones
    public List<RestrictionEntity> getRestrictions() {
        return restrictionRepository.findAll();
    }

    // Obtener solo restricciones activas (active = 1)
    public List<RestrictionEntity> getRestrictionsActive() {
        return restrictionRepository.findByActive(1);
    }

    // Guardar una nueva restricción
    public RestrictionEntity saveRestriction(RestrictionEntity restriction) {
        return restrictionRepository.save(restriction);
    }

    // Obtener restricción por ID (solo si está activa)
    public RestrictionEntity getRestrictionById(Long id) {
        return restrictionRepository.findById(id).orElse(null);
    }

    // Actualizar restricción
    public RestrictionEntity updateRestriction(RestrictionEntity restriction) {
        return restrictionRepository.save(restriction);
    }

    // Borrado lógico (Desactivar)
    public boolean deleteRestriction(Long id) throws Exception {
        try {
            RestrictionEntity restriction = restrictionRepository.findById(id).orElse(null);
            if (restriction != null) {
                restriction.setActive(0);
                restrictionRepository.save(restriction);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al desactivar la restricción: " + e.getMessage());
        }
    }
}