package com.travel.app.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PersonDTO {
    private Long id;
    private String fullName;
    private String identification;
    private String email;
    private String phone;
    private String nationality;
    private Integer active;
    private Long createdByUserId;
    private Long updatedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
