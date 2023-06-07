package com.example.shoeshop.service;

import com.example.shoeshop.entity.Availability;
import com.example.shoeshop.entity.Cart;
import com.example.shoeshop.entity.Shoes;
import com.example.shoeshop.entity.Size;
import com.example.shoeshop.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CartsServiceTest {

    private final CartsService cartsService;

    @Autowired
    CartsServiceTest(CartsService cartsService) {
        this.cartsService = cartsService;
    }

    @MockBean
    private CartsRepository cartsRepository;

    @MockBean
    private AvailabilitiesRepository availabilitiesRepository;

    @Captor
    private ArgumentCaptor<List<Long>> idsForDeleteCapture;

    @Test
    void buyTest1() {
        Long userId = -1L;
        List<String> errors = new ArrayList<>();

        Size size1 = new Size(10, 1, null, null);
        Size size2 = new Size(11, 1, null, null);
        Size size3 = new Size(12, 1, null, null);
        Size size4 = new Size(13, 1, null, null);

        Availability a1 = new Availability(1L, 2, null, size1);
        Availability a2 = new Availability(2L, 1, null, size2);
        Availability a3 = new Availability(3L, 1, null, size3);
        Availability a4 = new Availability(4L, 1, null, size4);
        Availability a5 = new Availability(5L, 1, null, size4);

        Cart cart1 = Cart.builder()
                .id(111L)
                .size(size1)
                .shoes(Shoes.builder()
                        .availabilities(List.of(a1, a2, a4))
                        .build())
                .build();

        Cart cart2 = Cart.builder()
                .id(112L)
                .size(size4)
                .shoes(Shoes.builder()
                        .availabilities(List.of(a2, a3, a5))
                        .build())
                .build();

        Mockito.doReturn(List.of(cart1, cart2))
                .when(cartsRepository)
                .findByUserIdWithShoesAndAvailability(ArgumentMatchers.eq(userId));

        cartsService.buy(userId, errors);

        Mockito.verify(cartsRepository, Mockito.times(1))
                .deleteAllByIdCustom(idsForDeleteCapture.capture());

        Mockito.verify(availabilitiesRepository, Mockito.times(1))
                .updateCountCustom(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1));

        Mockito.verify(availabilitiesRepository, Mockito.times(1))
                .deleteByIdCustom(ArgumentMatchers.eq(5L));

        List<Long> ids = idsForDeleteCapture.getValue();

        Assertions.assertEquals(2, ids.size());
        Assertions.assertArrayEquals(new Long[]{111L, 112L}, ids.toArray(Long[]::new));

        Assertions.assertEquals(0, errors.size());
    }

    @Test
    void buyTest2() {
        Long userId = -1L;
        List<String> errors = new ArrayList<>();

        Size size1 = new Size(10, 1, null, null);
        Size size2 = new Size(11, 1, null, null);

        Shoes shoes = Shoes.builder()
                .name("ShoesName")
                .build();

        Availability a1 = new Availability(1L, 0, shoes, size1);
        Availability a2 = new Availability(2L, 1, shoes, size2);

        shoes.setAvailabilities(List.of(a1, a2));

        Cart cart1 = Cart.builder()
                .id(111L)
                .size(size1)
                .shoes(shoes)
                .build();

        Mockito.doReturn(List.of(cart1))
                .when(cartsRepository)
                .findByUserIdWithShoesAndAvailability(ArgumentMatchers.eq(userId));

        cartsService.buy(userId, errors);

        Mockito.verify(cartsRepository, Mockito.times(1))
                .deleteAllByIdCustom(idsForDeleteCapture.capture());

        Mockito.verify(availabilitiesRepository, Mockito.times(0))
                .updateCountCustom(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());

        Mockito.verify(availabilitiesRepository, Mockito.times(0))
                .deleteByIdCustom(ArgumentMatchers.anyLong());

        List<Long> ids = idsForDeleteCapture.getValue();

        Assertions.assertEquals(1, ids.size());
        Assertions.assertArrayEquals(new Long[]{111L}, ids.toArray(Long[]::new));

        Assertions.assertEquals(1, errors.size());
    }
}