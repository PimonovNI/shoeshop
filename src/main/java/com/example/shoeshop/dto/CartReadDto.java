package com.example.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartReadDto {
    private Long id;
    private String name;
    private String brandName;
    private Double price;
    private String image;
    private Integer sizeId;
}
