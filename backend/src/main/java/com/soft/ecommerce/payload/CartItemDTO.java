package com.soft.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long Id;
    private Double discount;
    private Integer quantity;
    private String price;
    private ProductDTO product;
    private CartDTO cart;
}
