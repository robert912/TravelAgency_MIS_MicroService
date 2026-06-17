package com.travel.app.services;

import com.travel.app.entities.TravelTypeEntity;
import com.travel.app.repositories.TravelTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelTypeService {

    @Autowired
    TravelTypeRepository travelTypeRepository;

    // Listar todos tipos de viaje
    public List<TravelTypeEntity> getTravelTypes() {
        return travelTypeRepository.findAll();
    }

    // Listar solo tipos de viaje activos
    public List<TravelTypeEntity> getTravelTypesActive() {
        return travelTypeRepository.findByActive(1);
    }

    // Guardar nuevo tipo de viaje
    public TravelTypeEntity saveTravelType(TravelTypeEntity travelType) {
        return travelTypeRepository.save(travelType);
    }

    // Buscar por ID (solo si está activo)
    public TravelTypeEntity getTravelTypeById(Long id) {
        TravelTypeEntity travelType = travelTypeRepository.findById(id).orElse(null);
        if (travelType != null /*&& travelType.getActive() == 1*/) {
            return travelType;
        }
        return null;
    }

    // Actualizar tipo de viaje
    public TravelTypeEntity updateTravelType(TravelTypeEntity travelType) {
        return travelTypeRepository.save(travelType);
    }

}