package com.zamazor.market.modules.product.service;

import com.zamazor.market.modules.product.models.dto.CategoryDto;
import com.zamazor.market.modules.product.models.mapper.CategoryMapper;
import com.zamazor.market.modules.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	public List<CategoryDto> getAll() {
		return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
	}
}
