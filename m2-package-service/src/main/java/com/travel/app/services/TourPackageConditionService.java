package com.travel.app.services;

import com.travel.app.entities.ConditionEntity;
import com.travel.app.entities.TourPackageConditionEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.repositories.TourPackageConditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TourPackageConditionService {

    @Autowired
    private TourPackageConditionRepository tourPackageConditionRepository;

    @Autowired
    private TourPackageService tourPackageService;

    @Autowired
    private ConditionService conditionService;

    /**
     * Sincroniza las condiciones de un paquete
     * @param packageId ID del paquete turístico
     * @param conditionIds Lista de IDs de condiciones que debe tener el paquete
     * @param userId ID del usuario que realiza la operación
     */
    @Transactional
    public void syncPackageConditions(Long packageId, List<Long> conditionIds, Long userId) {
        // Validar que el paquete existe
        TourPackageEntity tourPackage = tourPackageService.getTourPackageById(packageId);
        if (tourPackage == null) {
            throw new RuntimeException("Paquete no encontrado con ID: " + packageId);
        }

        // Obtener todas las relaciones actuales del paquete
        List<TourPackageConditionEntity> existingRelations = tourPackageConditionRepository.findByTourPackageId(packageId);

        // Crear un mapa de conditionId -> relación existente
        Map<Long, TourPackageConditionEntity> existingMap = existingRelations.stream()
                .collect(Collectors.toMap(
                        rel -> rel.getCondition().getId(),
                        rel -> rel
                ));

        // IDs que vienen en la nueva lista (sin duplicados)
        List<Long> newConditionIds = conditionIds.stream().distinct().toList();
        LocalDateTime now = LocalDateTime.now();

        // 1. Procesar condiciones a agregar o reactivar
        for (Long conditionId : newConditionIds) {
            if (existingMap.containsKey(conditionId)) {
                // La relación existe, activarla si está inactiva
                TourPackageConditionEntity existing = existingMap.get(conditionId);
                if (existing.getActive() == 0) {
                    existing.setActive(1);
                    existing.setModifiedByUserId(userId);
                    existing.setUpdatedAt(now);
                    tourPackageConditionRepository.save(existing);
                }
            } else {
                // La relación no existe, crearla nueva
                ConditionEntity condition = conditionService.getConditionById(conditionId);
                if (condition == null) {
                    throw new RuntimeException("Condición no encontrada con ID: " + conditionId);
                }

                TourPackageConditionEntity newRelation = new TourPackageConditionEntity();
                newRelation.setTourPackage(tourPackage);
                newRelation.setCondition(condition);
                newRelation.setActive(1);
                newRelation.setCreatedByUserId(userId);
                newRelation.setModifiedByUserId(userId);
                newRelation.setCreatedAt(now);
                newRelation.setUpdatedAt(now);
                tourPackageConditionRepository.save(newRelation);
            }
        }

        // 2. Desactivar condiciones que ya no están en la lista
        for (Map.Entry<Long, TourPackageConditionEntity> entry : existingMap.entrySet()) {
            Long conditionId = entry.getKey();
            TourPackageConditionEntity existing = entry.getValue();

            if (!newConditionIds.contains(conditionId) && existing.getActive() == 1) {
                existing.setActive(0);
                existing.setModifiedByUserId(userId);
                existing.setUpdatedAt(now);
                tourPackageConditionRepository.save(existing);
            }
        }
    }
}