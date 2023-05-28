package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandsRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByName(String name);
}
