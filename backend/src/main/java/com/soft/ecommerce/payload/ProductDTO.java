package com.soft.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String description;
    private Double discount;
    private String image;
    private Double price;
    private String name;
    private Integer stock;
    private Double specialPrice;
}
