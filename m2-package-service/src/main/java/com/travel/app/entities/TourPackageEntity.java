package com.travel.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.travel.app.enums.PackageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "tour_package")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourPackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private SeasonEntity season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_type_id", nullable = false)
    private TravelTypeEntity travelType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date")
    private LocalDate endDate;

    @DecimalMin(value = "0.01")
    @Column(nullable = false)
    private BigDecimal price;

    @Min(1)
    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status = PackageStatus.DISPONIBLE;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer stars;

    @Column(name = "image_url")
    private String imageUrl;

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
        validateDates();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        validateDates();
        this.updatedAt = LocalDateTime.now();
    }

    private void validateDates() {
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be greater than start date");
        }
    }

    @JsonManagedReference
    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL)
    @SQLRestriction("active = 1")
    private List<TourPackageConditionEntity> conditions;

    @JsonManagedReference
    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL)
    @SQLRestriction("active = 1")
    private List<TourPackageRestrictionEntity> restrictions;

    @JsonManagedReference
    @OneToMany(mappedBy = "tourPackage", cascade = CascadeType.ALL)
    @SQLRestriction("active = 1")
    private List<TourPackageServiceEntity> services;
}