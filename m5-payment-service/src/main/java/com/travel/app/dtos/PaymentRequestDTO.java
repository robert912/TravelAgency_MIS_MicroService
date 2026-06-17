package com.travel.app.dtos;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long reservationId;
    private String cardNumber;
    private String cardHolderName;
    private String cardExpiration;
    private String cardCvv;
    private String paymentMethod;
    private Long userId;
}