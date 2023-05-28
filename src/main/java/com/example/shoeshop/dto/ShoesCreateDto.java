package com.example.shoeshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoesCreateDto {

    @NotEmpty(message = "Заповніть поле")
    private String name;

    private MultipartFile image;

    @NotEmpty(message = "Заповніть поле")
    private String description;

    @NotEmpty(message = "Оберіть приналежність")
    private String gender;

    private Integer brand;

    private String newBrand;

    @Min(value = 0, message = "Ціна повина бути більше за нуль")
    private Double price;

    private List<AvailabilityCreateDto> availabilities;

}
