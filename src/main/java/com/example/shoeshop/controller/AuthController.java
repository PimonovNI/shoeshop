package com.example.shoeshop.controller;

import com.example.shoeshop.dto.PersonCreateDto;
import com.example.shoeshop.service.PeopleService;
import com.example.shoeshop.util.validator.PeopleValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PeopleService peopleService;
    private final PeopleValidator peopleValidator;

    @Autowired
    public AuthController(PeopleService peopleService, PeopleValidator peopleValidator) {
        this.peopleService = peopleService;
        this.peopleValidator = peopleValidator;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model){
        model.addAttribute("message", false);
        return "/auth/login";
    }

    @GetMapping("/registration")
    public String showRegPage(Model model){
        model.addAttribute("person", new PersonCreateDto());
        return "/auth/reg";
    }

    @GetMapping("/activation/{code}")
    public String activationUser(@PathVariable("code") String code, Model model){
        boolean isActivated = peopleService.activation(code);
        model.addAttribute("exception", !isActivated);
        model.addAttribute("info", isActivated ? "Активація пройшла успішно" : "Активація провалилась");
        model.addAttribute("message", true);
        return "/auth/login";
    }

    @PostMapping("/registration")
    public String registrationNewUser(@ModelAttribute("person") @Valid PersonCreateDto person, BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes){
        peopleValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors())
            return "/auth/reg";
        peopleService.registration(person);
        redirectAttributes.addFlashAttribute("message", true);
        redirectAttributes.addFlashAttribute("exception", false);
        redirectAttributes.addFlashAttribute("info", "Підтвердіть дії в своїй пошті");
        return "redirect:/auth/login";
    }

}
