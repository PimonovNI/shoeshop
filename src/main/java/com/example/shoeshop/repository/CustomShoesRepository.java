package com.example.shoeshop.repository;

import com.example.shoeshop.entity.Gender;
import com.example.shoeshop.entity.Shoes;
import com.example.shoeshop.util.SortType;

import java.util.List;
import java.util.Optional;

public interface CustomShoesRepository {
    List<Shoes> findAllWithBrand();
    List<Shoes> findAllWithBrand(Integer pageNum, Integer countPerPage);
    Optional<Shoes> findByIdWithBrandAndSize(Long id);
    Optional<Shoes> findByIdWithAvailability(Long id);
    List<Shoes> findWithCriteria(List<String> brands, List<Gender> genders, Integer priceMin,
                                 Integer priceMax, Integer sizeMin, Integer sizeMax, SortType sortType,
                                 boolean isContain, Integer pageNum, Integer countPerPage);
}
