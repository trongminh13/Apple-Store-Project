package com.example.demojava.controller;

import com.example.demojava.model.DiscountCode;
import com.example.demojava.service.DiscountCodeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/discount-codes")
public class DiscountCodeController {

    @Autowired
    private DiscountCodeService discountCodeService;

    @GetMapping
    public String getDiscountCodes(Model model) {
        model.addAttribute("discountCodes", discountCodeService.getAllDiscountCodes());
        model.addAttribute("newDiscountCode", new DiscountCode());
        return "discount-codes";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("newDiscountCode", new DiscountCode());
        return "add-discount-code";
    }

    @PostMapping
    public String addDiscountCode(@Valid @ModelAttribute("newDiscountCode") DiscountCode discountCode, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "add-discount-code";
        }

        discountCodeService.saveDiscountCode(discountCode);
        redirectAttributes.addFlashAttribute("message", "Added new discount code successfully!");
        return "redirect:/discount-codes";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DiscountCode discountCode = discountCodeService.getDiscountCodeById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount code Id:" + id));
        model.addAttribute("discountCode", discountCode);
        return "edit-discount-code";
    }

    @PostMapping("/update/{id}")
    public String updateDiscountCode(@PathVariable Long id, @Valid @ModelAttribute("discountCode") DiscountCode discountCode, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "edit-discount-code";
        }

        discountCodeService.updateDiscountCode(discountCode);
        redirectAttributes.addFlashAttribute("message", "Updated discount code successfully!");
        return "redirect:/discount-codes";
    }

    @GetMapping("/delete/{id}")
    public String deleteDiscountCode(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        discountCodeService.deleteDiscountCode(id);
        redirectAttributes.addFlashAttribute("message", "Deleted discount code successfully!");
        return "redirect:/discount-codes";
    }
}