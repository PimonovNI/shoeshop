package com.example.shoeshop.controller;

import com.example.shoeshop.dto.AvailabilityCreateDto;
import com.example.shoeshop.dto.ShoesCreateDto;
import com.example.shoeshop.dto.ShoesUpdateDto;
import com.example.shoeshop.service.ShoesService;
import com.example.shoeshop.util.validator.ShoesValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ShoesService shoesService;
    private final ShoesValidator shoesValidator;

    @Autowired
    public AdminController(ShoesService shoesService, ShoesValidator shoesValidator) {
        this.shoesService = shoesService;
        this.shoesValidator = shoesValidator;
    }

    @GetMapping()
    public String showAllGoodsPage(Model model){
        model.addAttribute("shoes", shoesService.findAllShoesWithBrand());
        return "/admin/goods";
    }

    @GetMapping("/new")
    public String showCreatingPage(Model model){
        ShoesCreateDto dto = new ShoesCreateDto();
        dto.setAvailabilities(Collections.nCopies(10, new AvailabilityCreateDto()));
        model.addAttribute("action", "new");
        model.addAttribute("shoes", dto);
        model.addAttribute("brands", shoesService.findAllBrands());
        model.addAttribute("sizes", shoesService.findAllSizes());
        return "/admin/edit";
    }

    @GetMapping("/{id}/edit")
    public String showEditPage(@PathVariable("id") Long id, Model model){
        ShoesUpdateDto dto = shoesService.findByIdForUpdate(id);
        model.addAttribute("action", "edit");
        model.addAttribute("shoes", dto);
        model.addAttribute("brands", shoesService.findAllBrands());
        model.addAttribute("sizes", shoesService.findAllSizes());
        return "/admin/edit";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute("shoes") @Valid ShoesCreateDto shoes, BindingResult bindingResult,
                         Model model) throws IOException {
        shoesValidator.validate(shoes, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "new");
            model.addAttribute("brands", shoesService.findAllBrands());
            model.addAttribute("sizes", shoesService.findAllSizes());
            return "/admin/edit";
        }
        shoesService.save(shoes);
        return "redirect:/admin";
    }

    @PatchMapping("/edit")
    public String edit(@ModelAttribute("shoes") @Valid ShoesUpdateDto shoes, BindingResult bindingResult,
                       Model model) throws IOException {
        shoesValidator.validateUpdate(shoes, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "edit");
            model.addAttribute("brands", shoesService.findAllBrands());
            model.addAttribute("sizes", shoesService.findAllSizes());
            return "/admin/edit";
        }
        shoesService.update(shoes);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) throws IOException {
        shoesService.delete(id);
        return "redirect:/admin";
    }

}
