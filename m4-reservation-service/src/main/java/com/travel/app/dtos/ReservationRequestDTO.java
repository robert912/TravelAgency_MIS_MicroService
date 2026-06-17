package com.travel.app.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReservationRequestDTO {
    private Long personId;
    private String identification;
    private String fullName;
    private String email;
    private String phone;
    private String nationality;
    private Long tourPackageId;
    private Integer passengers;
    private String specialRequests;
    private BigDecimal subtotal;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private List<DiscountDetailDTO> discountsDetail;
    private List<PassengerDataDTO> passengersData;

    @Data
    public static class PassengerDataDTO {
        private Long personId;
        private String identification;
        private String fullName;
        private String email;
        private String phone;
        private String nationality;
    }

    @Data
    public static class DiscountDetailDTO {
        private String name;
        private String description;
        private BigDecimal amount;
    }
}