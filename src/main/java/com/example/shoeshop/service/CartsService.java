package com.example.shoeshop.service;

import com.example.shoeshop.dto.CartReadDto;
import com.example.shoeshop.entity.Availability;
import com.example.shoeshop.entity.Cart;
import com.example.shoeshop.entity.Shoes;
import com.example.shoeshop.repository.*;
import com.example.shoeshop.util.exceptions.NotContainShoesException;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CartsService {

    private final CartsRepository cartsRepository;
    private final ShoesRepository shoesRepository;
    private final PeopleRepository peopleRepository;
    private final SizesRepository sizesRepository;
    private final AvailabilitiesRepository availabilitiesRepository;

    @Autowired
    public CartsService(CartsRepository cartsRepository, ShoesRepository shoesRepository, PeopleRepository peopleRepository, SizesRepository sizesRepository, AvailabilitiesRepository availabilitiesRepository) {
        this.cartsRepository = cartsRepository;
        this.shoesRepository = shoesRepository;
        this.peopleRepository = peopleRepository;
        this.sizesRepository = sizesRepository;
        this.availabilitiesRepository = availabilitiesRepository;
    }

    public List<CartReadDto> findByUserId(Long userId){
        return cartsRepository.findByUserIdWithUserAndShoes(userId)
                .stream()
                .map(this::mapFrom)
                .toList();
    }

    @Transactional
    public void save(Long userId, Long shoesId, Integer sizeId) {
        Cart cart = Cart.builder()
                .shoes(shoesRepository.getReferenceById(shoesId))
                .user(peopleRepository.getReferenceById(userId))
                .size(sizesRepository.getReferenceById(sizeId))
                .build();
        cartsRepository.save(cart);
    }

    @Transactional
    public void buy(Long userId, List<String> errors) {
        cartsRepository
                .findByUserIdWithShoesAndAvailability(userId)
                .forEach(c ->
                {
                    try {
                        for (Availability a : c.getShoes().getAvailabilities()) {
                            if (a.getSize().getId().equals(c.getSize().getId())) {
                                if (a.getCount() > 0) {
                                    a.setCount(a.getCount() - 1);
                                    if (a.getCount() == 0)
                                        availabilitiesRepository.deleteByIdQuickly(a.getId());
                                    else
                                        availabilitiesRepository.updateCountQuickly(a.getId(), a.getCount());
                                    break;
                                } else {
                                    throw new NotContainShoesException("Взуття - " + a.getShoes().getName() + ", немає в наявності, оберіть інше взуття, розмір, чи почекайте, поки з'явиться в наявності.");
                                }
                            }
                        }
                    }
                    catch (NotContainShoesException e) {
                        errors.add(e.getMessage());
                    }
                    cartsRepository.deleteByIdQuickly(c.getId());
                });
    }

    @Transactional
    public void delete(Long userId, Long shoesId, Integer sizeId) {
        Cart cart = cartsRepository.findByUserAndShoesAndSize(
                peopleRepository.getReferenceById(userId),
                shoesRepository.getReferenceById(shoesId),
                sizesRepository.getReferenceById(sizeId)
        ).orElseThrow(IllegalArgumentException::new);
        cartsRepository.delete(cart);
    }

    private CartReadDto mapFrom(Tuple tuple) {
        Integer sizeId = tuple.get(1, Integer.class);
        return Optional.of(tuple.get(0, Shoes.class))
                .map(shoes -> new CartReadDto(
                        shoes.getId(),
                        shoes.getName(),
                        shoes.getBrand().getName(),
                        shoes.getPrice(),
                        shoes.getImage(),
                        sizeId
                ))
                .orElseThrow(IllegalArgumentException::new);
    }
}
