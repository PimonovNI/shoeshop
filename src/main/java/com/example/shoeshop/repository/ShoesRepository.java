package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Shoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoesRepository extends JpaRepository<Shoes, Long>, CustomShoesRepository {
    Optional<Shoes> findByName(String name);

    @Query("SELECT s FROM Shoes s WHERE s.name = :name AND s.id <> :id")
    Optional<Shoes> findByNameIgnoreThis(@Param("name") String name, @Param("id") Long id);

}
