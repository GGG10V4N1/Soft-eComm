package com.soft.ecommerce.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    @PositiveOrZero
    private Integer quantity;

    @NotNull
    @PositiveOrZero
    private Double price;

    @PositiveOrZero
    private Double specialPrice;

    @PositiveOrZero
    @DecimalMax("100.0")
    private Double discount;


    private String image;
}
