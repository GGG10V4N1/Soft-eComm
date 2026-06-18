package com.soft.ecommerce.payload;

import com.soft.ecommerce.model.Address;
import com.soft.ecommerce.model.OrderItem;
import com.soft.ecommerce.model.Payment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String email;
    private List<OrderItemDTO> orderItems;
    private LocalDate date;
    private PaymentDTO payment;
    private Double totalAmount;
    private String status;
    private Long addressId;
}
