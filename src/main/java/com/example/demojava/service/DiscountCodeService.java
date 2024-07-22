package com.example.demojava.service;

import com.example.demojava.model.DiscountCode;
import com.example.demojava.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiscountCodeService {

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    public List<DiscountCode> getAllDiscountCodes() {
        return discountCodeRepository.findAll();
    }

    public void saveDiscountCode(DiscountCode discountCode) {
        discountCodeRepository.save(discountCode);
    }

    public Optional<DiscountCode> getDiscountCodeById(Long id) {
        return discountCodeRepository.findById(id);
    }

    public void updateDiscountCode(DiscountCode discountCode) {
        discountCodeRepository.save(discountCode);
    }

    public void deleteDiscountCode(Long id) {
        discountCodeRepository.deleteById(id);
    }
}
