package com.sports.store.repository;


import com.sports.store.models.Category;
import com.sports.store.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    Boolean existsByName(String name);

    Page<Product> findByCategoryId(Long id, Pageable pageable);

    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
}
