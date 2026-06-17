package com.travel.app.entities;

import com.travel.app.dtos.PersonDTO;
import com.travel.app.dtos.TourPackageDTO;
import com.travel.app.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Transient
    private PersonDTO person;

    @Column(name = "tour_package_id", nullable = false)
    private Long tourPackageId;

    @Transient
    private TourPackageDTO tourPackage;

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDIENTE;

    // Nuevos campos para precios y descuentos
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "discount_details", columnDefinition = "TEXT")
    private String discountDetails; // JSON con detalles de descuentos

    @Column(name = "passengers_count")
    private Integer passengersCount = 1;

    @Column(name = "solicitudes", columnDefinition = "TEXT")
    private String solicitudes;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer active = 1;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(name = "modified_by_user_id")
    private Long modifiedByUserId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
