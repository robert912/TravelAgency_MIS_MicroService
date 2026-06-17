package com.travel.app.services;

import com.travel.app.entities.ServiceEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.entities.TourPackageServiceEntity;
import com.travel.app.repositories.TourPackageServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TourPackageServiceService {

    @Autowired
    private TourPackageServiceRepository tourPackageServiceRepository;

    @Autowired
    private TourPackageService tourPackageService;

    @Autowired
    private ServiceService serviceService;

    /**
     * Sincroniza los servicios de un paquete
     * @param packageId ID del paquete turístico
     * @param serviceIds Lista de IDs de servicios que debe tener el paquete
     * @param userId ID del usuario que realiza la operación
     */
    @Transactional
    public void syncPackageServices(Long packageId, List<Long> serviceIds, Long userId) {
        // Validar que el paquete existe
        TourPackageEntity tourPackage = tourPackageService.getTourPackageById(packageId);
        if (tourPackage == null) {
            throw new RuntimeException("Paquete no encontrado con ID: " + packageId);
        }

        // Obtener todas las relaciones actuales del paquete
        List<TourPackageServiceEntity> existingRelations = tourPackageServiceRepository.findByTourPackageId(packageId);

        // Crear un mapa de serviceId -> relación existente
        Map<Long, TourPackageServiceEntity> existingMap = existingRelations.stream()
                .collect(Collectors.toMap(
                        rel -> rel.getService().getId(),
                        rel -> rel
                ));

        // IDs que vienen en la nueva lista (sin duplicados)
        List<Long> newServiceIds = serviceIds.stream().distinct().collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        // 1. Procesar servicios a agregar o reactivar
        for (Long serviceId : newServiceIds) {
            if (existingMap.containsKey(serviceId)) {
                // La relación existe, activarla si está inactiva
                TourPackageServiceEntity existing = existingMap.get(serviceId);
                if (existing.getActive() == 0) {
                    existing.setActive(1);
                    existing.setModifiedByUserId(userId);
                    existing.setUpdatedAt(now);
                    tourPackageServiceRepository.save(existing);
                }
            } else {
                // La relación no existe, crearla nueva
                ServiceEntity service = serviceService.getServiceById(serviceId);
                if (service == null) {
                    throw new RuntimeException("Servicio no encontrado con ID: " + serviceId);
                }

                TourPackageServiceEntity newRelation = new TourPackageServiceEntity();
                newRelation.setTourPackage(tourPackage);
                newRelation.setService(service);
                newRelation.setActive(1);
                newRelation.setCreatedByUserId(userId);
                newRelation.setModifiedByUserId(userId);
                newRelation.setCreatedAt(now);
                newRelation.setUpdatedAt(now);
                tourPackageServiceRepository.save(newRelation);
            }
        }

        // 2. Desactivar servicios que ya no están en la lista
        for (Map.Entry<Long, TourPackageServiceEntity> entry : existingMap.entrySet()) {
            Long serviceId = entry.getKey();
            TourPackageServiceEntity existing = entry.getValue();

            if (!newServiceIds.contains(serviceId) && existing.getActive() == 1) {
                existing.setActive(0);
                existing.setModifiedByUserId(userId);
                existing.setUpdatedAt(now);
                tourPackageServiceRepository.save(existing);
            }
        }
    }
}