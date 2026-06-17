package com.travel.app.services;

import com.travel.app.entities.RestrictionEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.entities.TourPackageRestrictionEntity;
import com.travel.app.repositories.TourPackageRestrictionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TourPackageRestrictionService {

    @Autowired
    private TourPackageRestrictionRepository tourPackageRestrictionRepository;

    @Autowired
    private TourPackageService tourPackageService;

    @Autowired
    private RestrictionService restrictionService;

    /**
     * Sincroniza las restricciones de un paquete
     * @param packageId ID del paquete turístico
     * @param restrictionIds Lista de IDs de restricciones que debe tener el paquete
     * @param userId ID del usuario que realiza la operación
     */
    @Transactional
    public void syncPackageRestrictions(Long packageId, List<Long> restrictionIds, Long userId) {
        // Validar que el paquete existe
        TourPackageEntity tourPackage = tourPackageService.getTourPackageById(packageId);
        if (tourPackage == null) {
            throw new RuntimeException("Paquete no encontrado con ID: " + packageId);
        }

        // Obtener todas las relaciones actuales del paquete
        List<TourPackageRestrictionEntity> existingRelations = tourPackageRestrictionRepository.findByTourPackageId(packageId);

        // Crear un mapa de restrictionId -> relación existente
        Map<Long, TourPackageRestrictionEntity> existingMap = existingRelations.stream()
                .collect(Collectors.toMap(
                        rel -> rel.getRestriction().getId(),
                        rel -> rel
                ));

        // IDs que vienen en la nueva lista (sin duplicados)
        List<Long> newRestrictionIds = restrictionIds.stream().distinct().collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        // 1. Procesar restricciones a agregar o reactivar
        for (Long restrictionId : newRestrictionIds) {
            if (existingMap.containsKey(restrictionId)) {
                // La relación existe, activarla si está inactiva
                TourPackageRestrictionEntity existing = existingMap.get(restrictionId);
                if (existing.getActive() == 0) {
                    existing.setActive(1);
                    existing.setModifiedByUserId(userId);
                    existing.setUpdatedAt(now);
                    tourPackageRestrictionRepository.save(existing);
                }
            } else {
                // La relación no existe, crearla nueva
                RestrictionEntity restriction = restrictionService.getRestrictionById(restrictionId);
                if (restriction == null) {
                    throw new RuntimeException("Restricción no encontrada con ID: " + restrictionId);
                }

                TourPackageRestrictionEntity newRelation = new TourPackageRestrictionEntity();
                newRelation.setTourPackage(tourPackage);
                newRelation.setRestriction(restriction);
                newRelation.setActive(1);
                newRelation.setCreatedByUserId(userId);
                newRelation.setModifiedByUserId(userId);
                newRelation.setCreatedAt(now);
                newRelation.setUpdatedAt(now);
                tourPackageRestrictionRepository.save(newRelation);
            }
        }

        // 2. Desactivar restricciones que ya no están en la lista
        for (Map.Entry<Long, TourPackageRestrictionEntity> entry : existingMap.entrySet()) {
            Long restrictionId = entry.getKey();
            TourPackageRestrictionEntity existing = entry.getValue();

            if (!newRestrictionIds.contains(restrictionId) && existing.getActive() == 1) {
                existing.setActive(0);
                existing.setModifiedByUserId(userId);
                existing.setUpdatedAt(now);
                tourPackageRestrictionRepository.save(existing);
            }
        }
    }
}