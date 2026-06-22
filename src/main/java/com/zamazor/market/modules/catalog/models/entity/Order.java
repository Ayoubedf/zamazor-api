package com.zamazor.market.modules.catalog.models.entity;

import com.zamazor.market.modules.catalog.exception.IllegalOrderStateException;
import com.zamazor.market.modules.product.models.entity.Product;
import com.zamazor.market.modules.user.models.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(nullable = false)
	private BigDecimal total = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	private LocalDateTime createdAt;

	public void addOrderItem(OrderItem orderItem) {
		this.items.add(orderItem);
		orderItem.setOrder(this);
	}

	public static Order createFromCart(Cart cart) {
		Order order = new Order();
		order.setUser(cart.getUser());
		order.setStatus(OrderStatus.PENDING);
		order.setCreatedAt(LocalDateTime.now());

		BigDecimal calculatedTotal = BigDecimal.ZERO;
		for (CartItem cartItem : cart.getItems()) {
			Product product = cartItem.getProduct();
			product.deductStock(cartItem.getQuantity());

			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(product);
			orderItem.setUnitPrice(product.getPrice());
			orderItem.setQuantity(cartItem.getQuantity());

			order.addOrderItem(orderItem);

			BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
			calculatedTotal = calculatedTotal.add(itemTotal);
		}
		order.setTotal(calculatedTotal);

		return order;
	}

	public void cancel() {
		if (this.status != OrderStatus.PENDING && this.status != OrderStatus.CONFIRMED) {
			throw new IllegalOrderStateException("Order cannot be canceled in its current state: " + this.status);
		}

		for (OrderItem item : this.items) {
			item.getProduct().restoreStock(item.getQuantity());
		}

		this.status = OrderStatus.CANCELED;
	}
}