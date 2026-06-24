package com.zamazor.market.modules.product.controller;

import com.zamazor.market.modules.product.models.dto.CategoryDto;
import com.zamazor.market.modules.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping
	public List<CategoryDto> getAll() {
		return categoryService.getAll();
	}
}
