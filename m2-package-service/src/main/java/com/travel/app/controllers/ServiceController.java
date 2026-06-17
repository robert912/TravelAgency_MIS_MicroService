package com.travel.app.controllers;

import com.travel.app.entities.ServiceEntity;
import com.travel.app.services.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin("*")
public class ServiceController {

    @Autowired
    ServiceService serviceService;

    @GetMapping("/")
    public ResponseEntity<List<ServiceEntity>> listServices() {
        List<ServiceEntity> services = serviceService.getServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ServiceEntity>> listServicesActive() {
        List<ServiceEntity> services = serviceService.getServicesActive();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable Long id) {
        ServiceEntity service = serviceService.getServiceById(id);
        return service != null ? ResponseEntity.ok(service) : ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    public ResponseEntity<ServiceEntity> saveService(@RequestBody ServiceEntity service) {
        ServiceEntity newService = serviceService.saveService(service);
        return ResponseEntity.ok(newService);
    }

    @PutMapping("/")
    public ResponseEntity<ServiceEntity> updateService(@RequestBody ServiceEntity service) {
        ServiceEntity updatedService = serviceService.updateService(service);
        return ResponseEntity.ok(updatedService);
    }

}