package com.travel.app.services;

import com.travel.app.entities.ConfirmationEntity;
import com.travel.app.repositories.ConfirmationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConfirmationService {

    @Autowired
    private ConfirmationRepository confirmationRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String M4_BASE_URL = "http://m4-reservation-service/api/reservations";

    /** Fetch all reservations from M4. */
    public List<?> getAllReservations() {
        return restTemplate.exchange(
                M4_BASE_URL + "/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Object>>() {}
        ).getBody();
    }

    /** Fetch a single reservation from M4. */
    public Map<?, ?> getReservationById(Long id) {
        try {
            return restTemplate.getForObject(M4_BASE_URL + "/" + id, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    /** Fetch passengers of a reservation from M4. */
    public List<?> getPassengersByReservationId(Long id) {
        try {
            return restTemplate.exchange(
                    M4_BASE_URL + "/" + id + "/passengers",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {}
            ).getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    /** Fetch all reservations belonging to a person from M4. */
    public List<?> getReservationsByPersonId(Long personId) {
        try {
            return restTemplate.exchange(
                    M4_BASE_URL + "/person/" + personId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {}
            ).getBody();
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Change the status of a reservation:
     * 1. Delegate to M4 to perform the actual status transition.
     * 2. Record/update the change in this service's own ConfirmationEntity table.
     */
    public Map<?, ?> changeStatus(Long reservationId, String status, Long userId) {
        // Delegate to M4
        String url = M4_BASE_URL + "/" + reservationId + "/status?status=" + status + "&userId=" + userId;
        Map<?, ?> updated = restTemplate.exchange(url, HttpMethod.PUT, null, Map.class).getBody();

        // Persist the confirmation record in M6's own DB
        Optional<ConfirmationEntity> existing = confirmationRepository.findByReservationId(reservationId);
        ConfirmationEntity confirmation = existing.orElseGet(ConfirmationEntity::new);
        confirmation.setReservationId(reservationId);
        confirmation.setStatus(status);
        confirmation.setModifiedByUserId(userId);
        confirmationRepository.save(confirmation);

        return updated;
    }
}
