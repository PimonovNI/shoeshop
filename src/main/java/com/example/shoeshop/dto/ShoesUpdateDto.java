package com.example.shoeshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoesUpdateDto {

    private Long id;

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

    private byte isNewImage;

    public ShoesUpdateDto(Long id, String name, String description, String gender, Integer brand, Double price,
                          List<AvailabilityCreateDto> availabilities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.gender = gender;
        this.brand = brand;
        this.price = price;
        this.availabilities = Stream.concat(
                    availabilities.stream(),
                    Collections.nCopies(10 - availabilities.size(), new AvailabilityCreateDto()).stream()
                ).toList();
        this.isNewImage = 0;
    }
}
