package com.sports.store.repository;

import com.sports.store.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	List<User> getUsersByEmail(String email);

	List<User> getUsersByUsername(String username);

	Page<User> findByUsernameContaining(String username, Pageable pageable);
}
