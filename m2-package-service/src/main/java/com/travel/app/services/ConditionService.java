package com.travel.app.services;

import com.travel.app.entities.ConditionEntity;
import com.travel.app.repositories.ConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConditionService {

    @Autowired
    ConditionRepository conditionRepository;

    // Obtener todas las condiciones
    public List<ConditionEntity> getConditions() {
        return conditionRepository.findAll();
    }

    // Obtener solo condiciones activas
    public List<ConditionEntity> getConditionsActive() {
        return conditionRepository.findByActive(1);
    }

    // Guardar una condición
    public ConditionEntity saveCondition(ConditionEntity condition) {
        return conditionRepository.save(condition);
    }

    // Obtener condición por ID (solo si está activa)
    public ConditionEntity getConditionById(Long id) {
        ConditionEntity condition = conditionRepository.findById(id).orElse(null);
        if (condition != null) {
            return condition;
        }
        return null;
    }

    // Actualizar condición
    public ConditionEntity updateCondition(ConditionEntity condition) {
        return conditionRepository.save(condition);
    }

    // Borrado lógico (Desactivar)
    public boolean deleteCondition(Long id) throws Exception {
        try {
            ConditionEntity condition = conditionRepository.findById(id).orElse(null);
            if (condition != null) {
                condition.setActive(0);
                conditionRepository.save(condition);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al desactivar la condición: " + e.getMessage());
        }
    }
}