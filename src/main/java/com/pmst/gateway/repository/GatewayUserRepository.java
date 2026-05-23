package com.pmst.gateway.repository;

import com.pmst.gateway.model.GatewayUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GatewayUserRepository extends JpaRepository<GatewayUser, UUID> {
    Optional<GatewayUser> findByEmail(String email);
    Optional<GatewayUser> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
