package com.soft.ecommerce.service.impl;

import com.soft.ecommerce.exception.APIException;
import com.soft.ecommerce.exception.ResourceNotFoundException;
import com.soft.ecommerce.model.Cart;
import com.soft.ecommerce.model.CartItem;
import com.soft.ecommerce.model.Product;
import com.soft.ecommerce.payload.CartDTO;
import com.soft.ecommerce.payload.CartItemDTO;
import com.soft.ecommerce.payload.ProductDTO;
import com.soft.ecommerce.repository.CartItemRepository;
import com.soft.ecommerce.repository.CartRepository;
import com.soft.ecommerce.repository.ProductRepository;
import com.soft.ecommerce.service.api.CartService;
import com.soft.ecommerce.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final AuthUtil authUtil;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository, AuthUtil authUtil, ProductRepository productRepository, CartItemRepository cartItemRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.authUtil = authUtil;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }

    private Cart getCart() {
        Optional<Cart> userCart = cartRepository.findByEmail(authUtil.loggedInEmail());
        if (userCart.isPresent())  return userCart.get();

        Cart cart = new Cart();
        cart.setTotalAmount(0.00);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = getCart();

        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Optional<CartItem> cartItem = cartItemRepository.findByProductIdAndCartId(cart.getId(), productId);

        if(cartItem.isPresent()) {
            throw new APIException("Product with id: " + productId + " already exists in cart, use update endpoint to change quantity");
        }

        if (product.getStock() == 0){
            throw new APIException(product.getName() +  " is not available");
        }

        if(product.getStock() < quantity) {
            throw new APIException("Only " + product.getStock() + " items of " + product.getName() + " are available");
        }

        CartItem newCartItem = CartItem.builder()
                                       .cart(cart)
                                       .product(product)
                                       .quantity(quantity)
                                       .discount(product.getDiscount())
                                       .price(product.getPrice())
                                       .build();

        cartItemRepository.save(newCartItem);

        product.setStock(product.getStock());

        cart.setTotalAmount( cart.getTotalAmount() + (product.getPrice() * quantity) );

        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        cartDTO.setProducts( cartItems.stream()
                                      .map( item -> {
                                         ProductDTO prodDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                                          prodDTO.setStock(item.getQuantity());
                                         return prodDTO;
                                      })
                                      .toList()
        );

        return cartDTO;
    }

    @Override
    public List<CartDTO> findAllCarts() {
        return List.of();
    }

    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        return null;
    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        return "";
    }

    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        return "";
    }

    @Override
    public CartDTO findCartById() {
        return null;
    }
}
