package com.zamazor.market.modules.catalog.models.entity;

import com.zamazor.market.modules.catalog.exception.CartItemNotFoundException;
import com.zamazor.market.modules.catalog.exception.OutOfStockException;
import com.zamazor.market.modules.catalog.models.dto.GuestCartItemDto;
import com.zamazor.market.modules.product.models.entity.Product;
import com.zamazor.market.modules.user.models.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@OneToOne(mappedBy = "cart")
	private User user;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@BatchSize(size = 50)
	private List<CartItem> items = new ArrayList<>();

	@Version
	private Long version = 0L;

	public void mergeGuestCart(List<GuestCartItemDto> guestItems, Map<UUID, Product> productMap) {
		for (GuestCartItemDto guestItem : guestItems) {
			Product product = productMap.get(guestItem.productId());

			if (product == null || product.getStockQuantity() <= 0) continue;

			Optional<CartItem> existingItemOpt = this.items.stream()
					.filter(item -> item.getProduct().getId().equals(product.getId()))
					.findFirst();

			if (existingItemOpt.isPresent()) {
				CartItem existingItem = existingItemOpt.get();
				int targetQuantity = Math.min(existingItem.getQuantity() + guestItem.quantity(), product.getStockQuantity());
				existingItem.setQuantity(targetQuantity);
			} else {
				int targetQuantity = Math.min(guestItem.quantity(), product.getStockQuantity());
				if (targetQuantity > 0) {
					CartItem newItem = new CartItem();
					newItem.setCart(this);
					newItem.setProduct(product);
					newItem.setQuantity(targetQuantity);
					this.items.add(newItem);
				}
			}
		}
	}

	public void addProduct(Product product, int quantity) {
		if (product.getStockQuantity() < quantity) {
			throw new OutOfStockException("Insufficient stock for product: " + product.getName());
		}

		this.items.stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.findFirst()
				.ifPresentOrElse(
						existingItem -> existingItem.setQuantity(existingItem.getQuantity() + quantity),
						() -> {
							CartItem newItem = new CartItem();
							newItem.setCart(this);
							newItem.setProduct(product);
							newItem.setQuantity(quantity);
							this.items.add(newItem);
						}
				);
	}

	public void updateItemQuantity(Product product, Integer quantity) {
		if (product.getStockQuantity() < quantity) {
			throw new OutOfStockException("Insufficient stock for product: " + product.getName());
		}

		CartItem existingItem = this.items.stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.findFirst()
				.orElseThrow(CartItemNotFoundException::new);

		existingItem.setQuantity(quantity);
	}

	public void removeItem(UUID productId) {
		this.items.removeIf(item -> item.getProduct().getId().equals(productId));
	}

	public void clear() {
		this.items.clear();
	}

	public BigDecimal getSubtotal() {
		return this.items.stream()
				.map(CartItem::getLineTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}