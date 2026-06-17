package com.travel.app.services;

import com.travel.app.entities.ServiceEntity;
import com.travel.app.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    @Autowired
    ServiceRepository serviceRepository;

    // Listar solo servicios activos
    public List<ServiceEntity> getServices() {
        return serviceRepository.findAll();
    }

    // Listar solo servicios activos
    public List<ServiceEntity> getServicesActive() {
        return serviceRepository.findByActive(1);
    }

    // Guardar nuevo servicio
    public ServiceEntity saveService(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    // Buscar por ID (solo si está activo)
    public ServiceEntity getServiceById(Long id) {
        ServiceEntity service = serviceRepository.findById(id).orElse(null);
        if (service != null && service.getActive() == 1) {
            return service;
        }
        return null;
    }

    // Actualizar servicio
    public ServiceEntity updateService(ServiceEntity service) {
        return serviceRepository.save(service);
    }

}