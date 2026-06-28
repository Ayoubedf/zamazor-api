package com.zamazor.market.shared.api;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
		List<T> items,
		long totalElements,
		int totalPages,
		int page,
		int size
) {
	public PageResponse(@NonNull Page<T> pageResponse) {
		this(
				pageResponse.getContent(),
				pageResponse.getTotalElements(),
				pageResponse.getTotalPages(),
				pageResponse.getNumber(),
				pageResponse.getSize()
		);
	}
}