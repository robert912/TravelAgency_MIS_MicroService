package com.travel.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tour_package_restriction",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tour_package_id", "restriction_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourPackageRestrictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference //No vuelvas a mostrar el paquete desde aquí
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_package_id", nullable = false)
    private TourPackageEntity tourPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restriction_id", nullable = false)
    private RestrictionEntity restriction;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer active = 1;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(name = "modified_by_user_id")
    private Long modifiedByUserId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}