package com.example.shoeshop.service;

import com.example.shoeshop.dto.PersonCreateDto;
import com.example.shoeshop.entity.Person;
import com.example.shoeshop.entity.Role;
import com.example.shoeshop.repository.PeopleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PeopleServiceTest {

    private final PeopleService peopleService;

    @Autowired
    PeopleServiceTest(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @MockBean
    private PeopleRepository peopleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MailSenderService mailSender;

    @Test
    void registration() {
        String email = "test@test.com";
        String name = "TestName";
        String password = "testPassword";
        PersonCreateDto dto = new PersonCreateDto();
        dto.setEmail(email);
        dto.setUsername(name);
        dto.setPassword(password);

        peopleService.registration(dto);

        ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(ArgumentMatchers.anyString());
        Mockito.verify(mailSender, Mockito.times(1)).send(
                ArgumentMatchers.eq(email),
                ArgumentMatchers.eq("Вітаємо у нашому магазині взуття"),
                ArgumentMatchers.startsWith("Вітаємо, " + name)
        );
        Mockito.verify(peopleRepository, Mockito.times(1)).save(personCaptor.capture());

        Person person = personCaptor.getValue();
        Assertions.assertEquals(name, person.getUsername());
        Assertions.assertNotNull(person.getActivationCode());
        Assertions.assertEquals(Role.ROLE_USER, person.getRole());
    }

    @Test
    void activation() {
        Person person = new Person();
        String testActivationCode = "testActivationCode";
        person.setActivationCode(testActivationCode);

        Mockito.doReturn(Optional.of(person))
                .when(peopleRepository)
                .findByActivationCode(ArgumentMatchers.eq(testActivationCode));

        boolean isActivated = peopleService.activation(testActivationCode);
        Assertions.assertTrue(isActivated);
        Assertions.assertNull(person.getActivationCode());
    }

    @Test
    void activationFailed() {
        Person person = new Person();
        String testActivationCode = "testActivationCode";
        person.setActivationCode(testActivationCode);

        Mockito.doReturn(Optional.of(person))
                .when(peopleRepository)
                .findByActivationCode(ArgumentMatchers.eq("invalidTestActivationCode"));

        boolean isActivated = peopleService.activation(testActivationCode);
        Assertions.assertFalse(isActivated);
        Assertions.assertNotNull(person.getActivationCode());
    }
}