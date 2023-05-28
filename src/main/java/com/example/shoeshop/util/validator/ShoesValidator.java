package com.example.shoeshop.util.validator;

import com.example.shoeshop.dto.AvailabilityCreateDto;
import com.example.shoeshop.dto.ShoesCreateDto;
import com.example.shoeshop.dto.ShoesUpdateDto;
import com.example.shoeshop.repository.BrandsRepository;
import com.example.shoeshop.repository.ShoesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Component
public class ShoesValidator implements Validator {

    private final ShoesRepository shoesRepository;
    private final BrandsRepository brandsRepository;

    @Autowired
    public ShoesValidator(ShoesRepository shoesRepository, BrandsRepository brandsRepository) {
        this.shoesRepository = shoesRepository;
        this.brandsRepository = brandsRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ShoesCreateDto.class.equals(clazz) || ShoesUpdateDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ShoesCreateDto shoes = (ShoesCreateDto) target;

        if (shoes.getImage().isEmpty())
            errors.rejectValue("image", "", "Оберіть фото");

        if (shoes.getBrand() == -1 && (shoes.getNewBrand() == null || shoes.getNewBrand().isEmpty()))
            errors.rejectValue("brand", "", "Оберіть бренд чи введіть новий");

        if (shoes.getBrand() == -1 && brandsRepository.findByName(shoes.getNewBrand()).isPresent())
            errors.rejectValue("brand", "", "Такий бренд вже існує");

        if (shoesRepository.findByName(shoes.getName()).isPresent())
            errors.rejectValue("name", "", "Таке ім'я вже існує");

        Set<Integer> ids = new HashSet<>();
        boolean alreadyFound = false;
        for (AvailabilityCreateDto a : shoes.getAvailabilities()) {
            if (a.getSizeId() != -1 && (a.getCount() == null || a.getCount() < 0))
                errors.rejectValue("availabilities", "", "Кількість взуття має бути більша або дорівнювати 0");
            if (!alreadyFound && a.getSizeId() != -1){
                if (ids.contains(a.getSizeId())) {
                    alreadyFound = true;
                    errors.rejectValue("availabilities", "", "Значення розмірів мають не повторюватись");
                }
                else
                    ids.add(a.getSizeId());
            }
        }
    }

    public void validateUpdate(Object target, Errors errors) {
        ShoesUpdateDto shoes = (ShoesUpdateDto) target;

        if (shoes.getImage().isEmpty() && shoes.getIsNewImage() == 1)
            errors.rejectValue("image", "", "Оберіть фото");

        if (shoes.getBrand() == -1 && (shoes.getNewBrand() == null || shoes.getNewBrand().isEmpty()))
            errors.rejectValue("brand", "", "Оберіть бренд чи введіть новий");

        if (shoes.getBrand() == -1 && brandsRepository.findByName(shoes.getNewBrand()).isPresent())
            errors.rejectValue("brand", "", "Такий бренд вже існує");

        if (shoesRepository.findByNameIgnoreThis(shoes.getName(), shoes.getId()).isPresent())
            errors.rejectValue("name", "", "Таке ім'я вже існує");

        Set<Integer> ids = new HashSet<>();
        boolean alreadyFound = false;
        for (AvailabilityCreateDto a : shoes.getAvailabilities()) {
            if (a.getSizeId() != -1 && (a.getCount() == null || a.getCount() < 0))
                errors.rejectValue("availabilities", "", "Кількість взуття має бути більша або дорівнювати 0");
            if (!alreadyFound && a.getSizeId() != -1){
                if (ids.contains(a.getSizeId())) {
                    alreadyFound = true;
                    errors.rejectValue("availabilities", "", "Значення розмірів мають не повторюватись");
                }
                else
                    ids.add(a.getSizeId());
            }
        }
    }
}
