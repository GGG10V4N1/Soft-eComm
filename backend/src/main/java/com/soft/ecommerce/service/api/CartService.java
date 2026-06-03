package com.soft.ecommerce.service.api;


import com.soft.ecommerce.payload.CartDTO;
import com.soft.ecommerce.payload.CartItemDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    String createOrUpdateCartWithItems(List<CartItemDTO> cartItems);
    CartDTO addProductToCart(Long productId, Integer quantity);
    List<CartDTO> findAllCarts();
    CartDTO findCartById();
    String deleteProductFromCart(Long cartId, Long productId);
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);
    void updateProductInCarts(Long cartId, Long productId);
}
