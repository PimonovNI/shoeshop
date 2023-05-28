package com.example.shoeshop.util.validator;

import com.example.shoeshop.dto.PersonCreateDto;
import com.example.shoeshop.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PeopleValidator implements Validator {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleValidator(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonCreateDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonCreateDto person = (PersonCreateDto) target;

        if (peopleRepository.findByUsername(person.getUsername()).isPresent())
            errors.rejectValue("username", "", "Ім'я вже зайняте");

        if (!person.getPassword().matches(".*[a-z].*") ||
                !person.getPassword().matches(".*[A-Z].*") ||
                !person.getPassword().matches(".*\\d.*"))
            errors.rejectValue("password", "", "Використайте літери у різних реєстрах та число");

    }
}
