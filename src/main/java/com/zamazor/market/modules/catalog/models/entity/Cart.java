package com.zamazor.market.modules.catalog.models.entity;

import com.zamazor.market.modules.catalog.exception.CartItemNotFoundException;
import com.zamazor.market.modules.catalog.exception.OutOfStockException;
import com.zamazor.market.modules.product.models.entity.Product;
import com.zamazor.market.modules.user.models.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();

	public void addProduct(Product product, int quantity) {
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
				.orElseThrow(() -> new CartItemNotFoundException("Product not present in cart"));

		existingItem.setQuantity(quantity);
	}

	public void removeItem(UUID itemId) {
		this.items.removeIf(item -> item.getId().equals(itemId));
	}

	public void clear() {
		this.items.clear();
	}

	public BigDecimal getTotal() {
		return items.stream()
				.map(CartItem::getLineTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}