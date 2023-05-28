package com.example.shoeshop.service;

import com.example.shoeshop.entity.Person;
import com.example.shoeshop.repository.PeopleRepository;
import com.example.shoeshop.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUsername(username);
        if (person.isEmpty())
            throw new UsernameNotFoundException("Username was not find");

        if (person.get().getActivationCode() != null)
            throw new UsernameNotFoundException("User do not activate email");

        return new PersonDetails(person.get());
    }
}
