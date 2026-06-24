package com.zamazor.market.modules.product.controller;

import com.zamazor.market.modules.product.models.dto.CreateProductRequest;
import com.zamazor.market.modules.product.models.dto.ProductDto;
import com.zamazor.market.modules.product.models.dto.UpdateProductRequest;
import com.zamazor.market.modules.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/products")
@RestController
@RequiredArgsConstructor
public class ProductController {
	private final ProductService productService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProductDto> create(
			@Valid @ModelAttribute CreateProductRequest request,
			@RequestPart MultipartFile image,
			UriComponentsBuilder uriBuilder
	) throws IOException {
		var productDto = productService.create(request, image);
		var uri = uriBuilder.path("/products/{id}").buildAndExpand(productDto.id()).toUri();
		return ResponseEntity.created(uri).body(productDto);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductDto> getById(@PathVariable UUID id) {
		return ResponseEntity.ok(productService.getById(id));
	}

	@GetMapping
	public ResponseEntity<List<ProductDto>> getAll() {
		return ResponseEntity.ok(productService.getAll());
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<ProductDto>> getByCategory(@PathVariable UUID categoryId) {
		return ResponseEntity.ok(productService.getByCategory(categoryId));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProductDto> update(
			@PathVariable UUID id,
			@Valid @ModelAttribute UpdateProductRequest request,
			@RequestPart MultipartFile image
	) throws IOException {
		return ResponseEntity.ok(productService.update(id, request, image));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		productService.delete(id);
		return ResponseEntity.noContent().build();
	}
}