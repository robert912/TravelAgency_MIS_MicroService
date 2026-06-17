package com.travel.app.entities;

import com.travel.app.dtos.PersonDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reservation_passenger",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"reservation_id", "person_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPassengerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associated reservation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ReservationEntity reservation;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Transient
    private PersonDTO person;

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
