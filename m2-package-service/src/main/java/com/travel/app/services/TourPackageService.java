package com.travel.app.services;

import com.travel.app.entities.TourPackageEntity;
import com.travel.app.enums.PackageStatus;
import com.travel.app.repositories.TourPackageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TourPackageService {

    @Autowired
    TourPackageRepository tourPackageRepository;

    @Autowired
    private org.springframework.web.client.RestTemplate restTemplate;

    // Listar todos los paquetes turísticos con verificación de estado
    @Transactional
    public List<TourPackageEntity> getTourPackages() {
        List<TourPackageEntity> packages = tourPackageRepository.findAll();

        // Verificar y actualizar el estado de cada paquete
        for (TourPackageEntity pkg : packages) {
            updatePackageStatusIfNeeded(pkg);
        }

        return packages;
    }

    // Listar solo paquetes turísticos activos con verificación de estado
    @Transactional
    public List<TourPackageEntity> getTourPackagesActive() {
        List<TourPackageEntity> packages = tourPackageRepository.findByActive(1);

        // Verificar y actualizar el estado de cada paquete
        for (TourPackageEntity pkg : packages) {
            updatePackageStatusIfNeeded(pkg);
        }

        return packages;
    }

    /**
     * Verifica y actualiza el estado del paquete según disponibilidad y fechas
     * @param pkg Paquete a verificar
     * @return true si el estado cambió, false en caso contrario
     */
    @Transactional
    public boolean updatePackageStatusIfNeeded(TourPackageEntity pkg) {
        if (pkg == null || pkg.getStatus() == PackageStatus.CANCELADO) {
            return false; // No modificar paquetes cancelados manualmente
        }

        PackageStatus currentStatus = pkg.getStatus();
        PackageStatus newStatus = calculateAutomaticStatus(pkg);

        if (currentStatus != newStatus) {
            pkg.setStatus(newStatus);
            pkg.setUpdatedAt(LocalDateTime.now());
            tourPackageRepository.save(pkg);

            // Log para seguimiento
            System.out.println("Paquete #" + pkg.getId() + " (" + pkg.getName() +
                    ") actualizado: " + currentStatus + " → " + newStatus);
            return true;
        }

        return false;
    }

    /**
     * Calcula el estado automático del paquete según reglas de negocio
     * @param pkg Paquete a evaluar
     * @return Estado calculado
     */
    private PackageStatus calculateAutomaticStatus(TourPackageEntity pkg) {
        // Regla 1: Verificar si la fecha ya venció
        if (isDateExpired(pkg.getStartDate())) {
            return PackageStatus.NO_VIGENTE;
        }

        // Regla 2: Verificar disponibilidad de cupos
        int availableSlots = getAvailableSlots(pkg.getId());
        if (availableSlots <= 0) {
            return PackageStatus.AGOTADO;
        }

        // Regla 3: Está disponible
        return PackageStatus.DISPONIBLE;
    }

    /**
     * Verifica si una fecha ya expiró
     */
    private boolean isDateExpired(LocalDate startDate) {
        if (startDate == null) return false;
        LocalDate today = LocalDate.now();
        return !startDate.isAfter(today);
    }

    /**
     * Calcula los cupos disponibles de un paquete en tiempo real
     * @param packageId ID del paquete
     * @return Cupos disponibles
     */
    public int getAvailableSlots(Long packageId) {
        TourPackageEntity tourPackage = getTourPackageById(packageId);
        if (tourPackage == null || tourPackage.getTotalSlots() == null) {
            return 0;
        }

        Integer confirmedReservations;
        try {
            confirmedReservations = restTemplate.getForObject(
                "http://m4-reservation-service/api/reservations/count-confirmed-passengers?packageId=" + packageId,
                Integer.class
            );
        } catch (Exception e) {
            System.err.println("Error calling reservation service: " + e.getMessage());
            confirmedReservations = 0;
        }
        if (confirmedReservations == null) {
            confirmedReservations = 0;
        }
        return tourPackage.getTotalSlots() - confirmedReservations;
    }

    // Listar todos los paquetes turísticos
    //public List<TourPackageEntity> getTourPackages() {
    //    return tourPackageRepository.findAll();
    //}

    // Listar solo paquetes turísticos activos
    //public List<TourPackageEntity> getTourPackagesActive() {
    //    return tourPackageRepository.findByActive(1);
    //}

    // Guardar nuevo paquete turístico
    public TourPackageEntity saveTourPackage(TourPackageEntity tourPackage) {
        return tourPackageRepository.save(tourPackage);
    }

    // Buscar por ID (solo si está activo)
    public TourPackageEntity getTourPackageById(Long id) {
        TourPackageEntity tourPackage = tourPackageRepository.findById(id).orElse(null);
        if (tourPackage != null) {// && tourPackage.getActive() == 1) {
            return tourPackage;
        }
        return null;
    }

    // Actualizar paquete turístico
    public TourPackageEntity updateTourPackage(TourPackageEntity tourPackage) {
        return tourPackageRepository.save(tourPackage);
    }

    // Borrado lógico (Desactivar)
    public boolean deleteTourPackage(Long id) throws Exception {
        try {
            TourPackageEntity tourPackage = tourPackageRepository.findById(id).orElse(null);
            if (tourPackage != null) {
                tourPackage.setActive(0);
                tourPackageRepository.save(tourPackage);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al desactivar el paquete turístico: " + e.getMessage());
        }
    }

    // Buscar Paquetes filtrados por destino, fechas de viaje, rango de precios, duración o tipo de experiencia.
    public List<TourPackageEntity> filterTourPackages(String destination, BigDecimal minPrice, BigDecimal maxPrice, LocalDate startDate, LocalDate endDate, Long travelTypeId) {
        return tourPackageRepository.findByFilters(destination, minPrice, maxPrice, startDate, endDate, travelTypeId);
    }
}