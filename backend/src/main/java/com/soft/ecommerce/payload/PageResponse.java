package com.soft.ecommerce.payload;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> content;
    private Long totalElements;
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Boolean lastPage;
}
