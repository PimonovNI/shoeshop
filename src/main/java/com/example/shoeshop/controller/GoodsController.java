package com.example.shoeshop.controller;

import com.example.shoeshop.entity.Gender;
import com.example.shoeshop.service.ShoesService;
import com.example.shoeshop.util.SortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GoodsController {

    private final ShoesService shoesService;

    @Autowired
    public GoodsController(ShoesService shoesService) {
        this.shoesService = shoesService;
    }

    @GetMapping("/")
    public String showAllGoodsPage(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                   Model model) {
        model.addAttribute("shoes", shoesService.findAllShoesWithBrand(page));
        model.addAttribute("brands", shoesService.findAllBrands());
        model.addAttribute("page", page);
        return "/content/main";
    }

    @GetMapping("/filter")
    public String showFilteredGoodsPage(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                        @RequestParam(name = "sort", required = false, defaultValue = "NONE") SortType sortType,
                                        @RequestParam(name = "gender", required = false) List<Gender> genders,
                                        @RequestParam(name = "min_price", required = false) String priceMin,
                                        @RequestParam(name = "max_price", required = false) String priceMax,
                                        @RequestParam(name = "min_size", required = false) String sizeMin,
                                        @RequestParam(name = "max_size", required = false) String sizeMax,
                                        @RequestParam(name = "brand", required = false) List<String> brands,
                                        @RequestParam(name = "isContain", required = false, defaultValue = "on") String isContain,
                                        Model model){
        model.addAttribute("shoes", shoesService.findWithCriteria(
                brands, genders, parseStringToInt(priceMin), parseStringToInt(priceMax),
                parseStringToInt(sizeMin), parseStringToInt(sizeMax), sortType, isContain, page));
        model.addAttribute("brands", shoesService.findAllBrands());
        model.addAttribute("page", page);
        model.addAttribute("filter", true);
        return "/content/main";
    }

    @GetMapping("/shoes_details/{id}")
    public String shoeShoesDetailsPage(@PathVariable("id") Long id, Model model) {
        model.addAttribute("shoes", shoesService.findShoesDetailsById(id).orElseThrow(IllegalArgumentException::new));
        return "/content/product";
    }

    private Integer parseStringToInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
