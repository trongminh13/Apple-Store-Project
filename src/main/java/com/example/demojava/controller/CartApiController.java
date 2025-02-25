package com.example.demojava.controller;

import com.example.demojava.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    @Autowired
    private CartService cartService;

    @GetMapping("/count")
    public int getCartItemCount() {
        return cartService.getCartItemCount();
    }
}
