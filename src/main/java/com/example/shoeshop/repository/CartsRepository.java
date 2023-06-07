package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Cart;
import com.example.shoeshop.entity.Person;
import com.example.shoeshop.entity.Shoes;
import com.example.shoeshop.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CartsRepository extends JpaRepository<Cart, Long>, CustomCartRepository {
    Optional<Cart> findByUserAndShoesAndSize(Person person, Shoes shoes, Size size);
    @Modifying
    @Query(value = "DELETE FROM Cart c WHERE c.id IN :ids")
    void deleteAllByIdCustom(@Param("ids") List<Long> id);
    List<Cart> findAllByUser(Person person);
}
