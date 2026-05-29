package com.soft.ecommerce.controller;

import com.soft.ecommerce.payload.CartDTO;
import com.soft.ecommerce.payload.CartItemDTO;
import com.soft.ecommerce.service.api.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/ecomApi")
@RestController
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity)  {

        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDTO);
    }

    @PostMapping("/cart/create")
    public ResponseEntity<String> createOrUpdateCartItem(@RequestBody List<CartItemDTO> cartItems) {

        String response = cartService.createOrUpdateCartWithItems(cartItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> findAllCarts() {

        List<CartDTO> cartDTOs = cartService.findAllCarts();
        return ResponseEntity.status(HttpStatus.OK).body(cartDTOs);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> findCartById(){

        CartDTO cartDTO = cartService.findCartById();
        return ResponseEntity.status(HttpStatus.OK).body(cartDTO);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId, @PathVariable String operation) {

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                                                                  operation.equalsIgnoreCase("delete") ? -1 : 1);
        return ResponseEntity.status(HttpStatus.OK).body(cartDTO);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {

        String status = cartService.deleteProductFromCart(cartId, productId);
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }
}
