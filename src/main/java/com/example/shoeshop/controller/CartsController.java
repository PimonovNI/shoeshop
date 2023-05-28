package com.example.shoeshop.controller;

import com.example.shoeshop.dto.CartReadDto;
import com.example.shoeshop.dto.PersonReadDto;
import com.example.shoeshop.security.PersonDetails;
import com.example.shoeshop.service.CartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartsController {

    private final CartsService cartsService;

    @Autowired
    public CartsController(CartsService cartsService) {
        this.cartsService = cartsService;
    }

    @GetMapping()
    public String showCartPageWithoutId(){
         return "redirect:/cart/" + getIdFromSession();
    }

    @GetMapping("/{id}")
    public String showCartPageWithId(@PathVariable("id") Long id, Model model) {
        if (!getIdFromSession().equals(id)) return "redirect:/";

        List<CartReadDto> shoes = cartsService.findByUserId(id);
        model.addAttribute("user", getUserInfo());
        model.addAttribute("shoes", shoes);
        model.addAttribute("sum", shoes.stream().map(CartReadDto::getPrice).reduce(Double::sum).orElse(0.));

        return "content/cart";
    }

    @PostMapping("/add_new/{shoesId}")
    public String addShoes(@PathVariable("shoesId") Long shoesId, @ModelAttribute("size") Integer sizeId) {
        Long userId = getIdFromSession();
        cartsService.save(userId, shoesId, sizeId);
        return "redirect:/";
    }

    @PatchMapping("/{userId}/buy")
    public String buyShoes(@PathVariable("userId") Long userId, Model model) {
        if (!getIdFromSession().equals(userId)) return "redirect:/";

        List<String> errors = new ArrayList<>();
        cartsService.buy(userId, errors);

        if (!errors.isEmpty()){
            model.addAttribute("errors", errors);
            model.addAttribute("user", getUserInfo());
            return "content/cart";
        }

        return "redirect:/cart/" + userId;
    }

    @DeleteMapping("/{userId}/delete/{shoesId}")
    public String deleteFromCart(@PathVariable("userId") Long userId, @PathVariable("shoesId") Long shoesId,
                                 @ModelAttribute("sizeId") Integer sizeId){
        if (!getIdFromSession().equals(userId)) return "redirect:/";
        cartsService.delete(userId, shoesId, sizeId);
        return "redirect:/cart/" + userId;
    }

    private Long getIdFromSession(){
        return ((PersonDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getId();
    }

    private PersonReadDto getUserInfo(){
        PersonDetails personDetails = (PersonDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return new PersonReadDto(
                personDetails.getId(),
                personDetails.getUsername(),
                personDetails.getEmail()
        );
    }

}
