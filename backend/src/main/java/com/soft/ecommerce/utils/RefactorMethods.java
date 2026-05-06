package com.soft.ecommerce.utils;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.payload.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public final class RefactorMethods {

    private RefactorMethods() {}

    public static Pageable buildPageable(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                              Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sortByAndOrder);
    }

    public static <E, D> PageResponse<D> getPageResponse(Page<E> page, Function<E, D> mapper,String message) {
        List<E> content = page.getContent();

        if(content.isEmpty()) throw new APIException(message);
        List<D> dtos = content.stream()
                              .map(mapper)
                              .toList();

        return PageResponse.<D>builder()
                           .content(dtos)
                           .totalElements(page.getTotalElements())
                           .pageSize(page.getSize())
                           .pageNumber(page.getNumber())
                           .totalPages(page.getTotalPages())
                           .lastPage(page.isLast())
                           .build();
    }

}
