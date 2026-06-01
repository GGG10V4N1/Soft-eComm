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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private Cart getOrCreateCart() {
        Optional<Cart> userCart = cartRepository.findByEmail(authUtil.loggedInEmail());
        if (userCart.isPresent())  return userCart.get();

        Cart cart = Cart.builder()
                        .totalAmount(0.00)
                        .user(authUtil.loggedInUser())
                        .cartItems(new ArrayList<>())
                        .build();
        return cartRepository.save(cart);
    }

    private CartDTO cartToCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> cartProducts = cartItems.stream()
                                                 .map( item -> {
                                                    ProductDTO prodDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                                                    prodDTO.setQuantity(item.getQuantity());
                                                    return prodDTO;
                                                 })
                                                 .toList();
        cartDTO.setProducts(cartProducts);
        return cartDTO;
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = getOrCreateCart();

        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Optional<CartItem> cartItem = cartItemRepository.findByProductIdAndCartId(productId, cart.getId());

        if(cartItem.isPresent())
            throw new APIException("Product " + product.getName() + " already exists in the cart");

        if (product.getQuantity() == 0)
            throw new APIException(product.getName() +  " is not available");

        if(product.getQuantity() < quantity)
            throw new APIException("Only " + product.getQuantity() + " items of " + product.getName() + " are available");

        CartItem newCartItem = CartItem.builder()
                                       .cart(cart)
                                       .product(product)
                                       .quantity(quantity)
                                       .discount(product.getDiscount())
                                       .price(product.getSpecialPrice())
                                       .build();

        cart.setTotalAmount( cart.getTotalAmount() + (product.getPrice() * quantity) );
        cart.addCartItem(newCartItem);
        Cart savedCart = cartRepository.save(cart);

        return cartToCartDTO(savedCart);
    }

    @Override
    public List<CartDTO> findAllCarts() {
        List<Cart> carts = (List<Cart>) cartRepository.findAll();
        if(carts.isEmpty())
            throw new APIException("No carts exist");

        return carts.stream()
                    .map(this::cartToCartDTO)
                    .toList();
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findByEmail(emailId)
                                      .orElseThrow(() -> new ResourceNotFoundException("Cart", "email", emailId));
        Long cartId  = userCart.getId();

        Cart cart = cartRepository.findById(cartId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        Product product = productRepository.findById(productId)
                                           .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if(product.getQuantity() == 0)
            throw new APIException(product.getName() +  " is not available");
        if(product.getQuantity() < quantity)
            throw new APIException("Only " + product.getQuantity() + " items of " + product.getName() + " are available");

        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(productId, cartId)
                                              .orElseThrow(() -> new APIException("Product " + product.getName() +
                                                                                   " not available in the cart!!!"));
        int newQuantity = cartItem.getQuantity() + quantity;
        if(newQuantity < 0)
            throw new APIException("Quantity cannot be negative");
        if(newQuantity > product.getQuantity())
            throw new APIException("Only " + product.getQuantity() + " items of " + product.getName() + " are available");

        if(newQuantity == 0){
            cart.setTotalAmount(cart.getTotalAmount() + (product.getSpecialPrice() * quantity));
            cart.removeCartItem(cartItem);
        }else{
            cartItem.setPrice(product.getSpecialPrice());
            cartItem.setQuantity(newQuantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalAmount(cart.getTotalAmount() + (product.getSpecialPrice() * quantity));
            cartRepository.save(cart);
        }

        return cartToCartDTO(cart);
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(productId, cartId)
                                              .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        cart.setTotalAmount(cart.getTotalAmount() - (cartItem.getPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteByProductIdAndCartId(productId, cartId);
        return "Product " + cartItem.getProduct().getName() + "removed from the cart";
    }

    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        return "";
    }

    @Override
    public CartDTO findCartById() {
        String emailId = authUtil.loggedInEmail();
        Cart cartUser = cartRepository.findByEmail(emailId)
                                      .orElseThrow(() -> new ResourceNotFoundException("Cart", "email", emailId));
        Long cartId = cartUser.getId();

        Cart foundedCart = cartRepository.findByEmailAndCartId(emailId, cartId)
                                         .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
        return cartToCartDTO(foundedCart);
    }
}
