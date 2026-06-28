package com.zamazor.market.modules.product.controller;

import com.zamazor.market.modules.product.models.dto.CategoryDto;
import com.zamazor.market.modules.product.models.dto.CategoryRequest;
import com.zamazor.market.modules.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/categories")
@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryRequest request) {
		var response = categoryService.create(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<CategoryDto>> getAll() {
		return ResponseEntity.ok(categoryService.getAll());
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryDto> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		categoryService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
