package com.example.shoeshop.service;

import com.example.shoeshop.dto.PersonCreateDto;
import com.example.shoeshop.entity.Person;
import com.example.shoeshop.entity.Role;
import com.example.shoeshop.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    @Value("${spring.site.url}")
    private String url;

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, MailSenderService mailSenderService) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    @Transactional
    public void registration(PersonCreateDto createDto){
        String code = UUID.randomUUID().toString();
        Person person = Person.builder()
                .username(createDto.getUsername())
                .email(createDto.getEmail())
                .password(passwordEncoder.encode(createDto.getPassword()))
                .role(Role.ROLE_USER)
                .activationCode(code)
                .build();

        mailSenderService.send(createDto.getEmail(), "Вітаємо у нашому магазині взуття", String.format(
                "Вітаємо, %s. Ви заєструвалися у нашому магазині взуття. Для підтверження пошти перейдіть за посиланням " +
                        "та звершіть реєстрацію:\n http://%s/auth/activation/%s\n Гарного вам дня!",
                createDto.getUsername(), url, code
        ));

        peopleRepository.save(person);
    }

    @Transactional
    public boolean activation(String code){
        Optional<Person> person = peopleRepository.findByActivationCode(code);
        if (person.isEmpty())
            return false;
        person.get().setActivationCode(null);
        return true;
    }

}
