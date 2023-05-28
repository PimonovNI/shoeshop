package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Cart;
import jakarta.persistence.Tuple;

import java.util.List;

public interface CustomCartRepository {
    List<Tuple> findByUserIdWithUserAndShoes(Long userId);
    List<Cart> findByUserIdWithShoesAndAvailability(Long userId);
}
