package com.soft.ecommerce.service.api;

import com.soft.ecommerce.payload.OrderDTO;
import com.soft.ecommerce.payload.OrderRequestDTO;
import com.soft.ecommerce.payload.PageResponse;

public interface OrderService {
    OrderDTO placeOrder(OrderRequestDTO orderRequestDTO, String paymentMethod);
    OrderDTO updateOrder(Long orderId, String status);
    PageResponse<OrderDTO> findAllSellerOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    PageResponse<OrderDTO> findAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    PageResponse<OrderDTO> findOrdersByUserEmail(String email, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    OrderDTO findOrderByIdForUser(Long orderId, String email);
}
