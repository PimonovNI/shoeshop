package com.example.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoesDetailsReadDto {

    private Long id;
    private String name;
    private String image;
    private String description;
    private String gender;
    private String brand;
    private Double price;
    private List<SizeReadDto> sizes;

}
