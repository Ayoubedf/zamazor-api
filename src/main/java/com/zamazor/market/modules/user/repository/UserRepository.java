package com.zamazor.market.modules.user.repository;

import com.zamazor.market.modules.user.models.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
	@EntityGraph(attributePaths = {"address"})
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
