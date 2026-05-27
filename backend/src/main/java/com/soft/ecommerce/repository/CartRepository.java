package com.soft.ecommerce.repository;

import com.soft.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart,Long> {
    @Query("SELECT c FROM Cart c WHERE c.user.email = :email")
    Optional<Cart> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM Cart c WHERE c.user.email = :email AND c.id = :cartId")
    Optional<Cart> findByEmailAndCartId(@Param("email") String email, @Param("cartId") Long cartId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id = :productId")
    List<Cart> findAllByProductId(@Param("productId") Long productId);
}
