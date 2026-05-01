package com.soft.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ProductResponse {
    private List<ProductDTO> content;
    private Long totalElements;
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Boolean lastPage;
}
