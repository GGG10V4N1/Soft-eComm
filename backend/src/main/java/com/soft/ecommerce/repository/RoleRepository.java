package com.soft.ecommerce.repository;

import com.soft.ecommerce.model.AppRole;
import com.soft.ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(AppRole appRole);
}
