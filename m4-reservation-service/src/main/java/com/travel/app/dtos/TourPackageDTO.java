package com.travel.app.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TourPackageDTO {
    private Long id;
    private String name;
    private String destination;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private Integer totalSlots;
    private String status;
    private Integer stars;
    private String imageUrl;
    private Integer active;
}
