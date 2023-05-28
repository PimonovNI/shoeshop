package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilitiesRepository extends JpaRepository<Availability, Long> {
    @Query(value = "SELECT a FROM Availability a INNER JOIN a.shoes sh INNER JOIN FETCH a.size s WHERE sh.id = :shoesId")
    List<Availability> findByShoesIdEquals(@Param("shoesId") Long shoesId);

    @Modifying
    @Query(value = "UPDATE Availability a SET a.count = :count WHERE a.id = :id")
    void updateCountQuickly(@Param("id") Long id, @Param("count") Integer count);

    @Modifying
    @Query(value = "DELETE FROM Availability a WHERE a.id = :id")
    void deleteByIdQuickly(@Param("id") Long id);
}
