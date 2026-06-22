package com.zamazor.market.modules.product.service;

import com.zamazor.market.domain.media.model.StoredMediaMetadata;
import com.zamazor.market.domain.media.ports.MediaStoragePort;
import com.zamazor.market.modules.product.exception.CategoryNotFoundException;
import com.zamazor.market.modules.product.exception.ProductNotFoundException;
import com.zamazor.market.modules.product.models.dto.CreateProductRequest;
import com.zamazor.market.modules.product.models.dto.ProductDto;
import com.zamazor.market.modules.product.models.dto.UpdateProductRequest;
import com.zamazor.market.modules.product.models.mapper.ProductMapper;
import com.zamazor.market.modules.product.repository.CategoryRepository;
import com.zamazor.market.modules.product.repository.ProductRepository;
import com.zamazor.market.modules.product.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final StoreRepository storeRepository;
	private final ProductMapper productMapper;
	private final MediaStoragePort mediaStorage;

	public List<ProductDto> getByCategory(UUID categoryId) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new CategoryNotFoundException("Category with id: " + categoryId + " not found");
		}
		return productRepository.findByCategoryId(categoryId).stream()
				.map(productMapper::toDto)
				.toList();
	}

	public ProductDto getById(UUID id) {
		return productRepository.findById(id)
				.map(productMapper::toDto)
				.orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + " not found"));
	}

	public List<ProductDto> getAll() {
		return productRepository.findAllWithAssociations().stream().map(productMapper::toDto).toList();
	}

	@Transactional
	public ProductDto create(CreateProductRequest request, @NonNull MultipartFile  image) throws IOException {
		var product = productMapper.toEntity(request);
		var category =  categoryRepository.findById(request.categoryId())
				.orElseThrow(() -> new CategoryNotFoundException("Category with id: " + request.categoryId() + " not found"));
		var store = storeRepository.findOne()
				.orElseThrow(() -> new IllegalStateException("The single store instance is missing from the database!"));

		product.setCategory(category);
		product.setStore(store);
		StoredMediaMetadata metadata = mediaStorage.uploadImage(image.getInputStream(), image.getOriginalFilename(), "products");
		String imageUrl = metadata.secureUrl();
		product.setImageUrl(imageUrl);

		return productMapper.toDto(productRepository.save(product));
	}

	@Transactional
	public ProductDto update(UUID id, @NonNull UpdateProductRequest request, MultipartFile image) throws IOException {
		var product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + " not found"));
		if (request.categoryId() != null && !request.categoryId().equals(product.getCategory().getId())) {
			var category = categoryRepository.findById(request.categoryId())
					.orElseThrow(() -> new CategoryNotFoundException("Category with id: " + request.categoryId() + " not found"));
			product.setCategory(category);
		}
		StoredMediaMetadata metadata = mediaStorage.uploadImage(image.getInputStream(), image.getOriginalFilename(), "products");
		String imageUrl = metadata.secureUrl();
		product.setImageUrl(imageUrl);

		product.setName(request.name());
		product.setPrice(request.price());
		product.setDescription(request.description());
		product.setStockQuantity(request.stockQuantity());

		return productMapper.toDto(product);
	}

	public void delete(UUID id) {
		if (!productRepository.existsById(id)) {
			throw new ProductNotFoundException("Product with id: " + id + " not found");
		}
		productRepository.deleteById(id);
	}
}
