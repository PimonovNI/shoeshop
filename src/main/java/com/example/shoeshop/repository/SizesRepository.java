package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizesRepository extends JpaRepository<Size, Integer> {
}
