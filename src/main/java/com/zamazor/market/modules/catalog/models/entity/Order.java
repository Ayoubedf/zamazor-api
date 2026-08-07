package com.zamazor.market.modules.catalog.models.entity;

import com.zamazor.market.modules.catalog.exception.IllegalOrderStateException;
import com.zamazor.market.modules.catalog.models.dto.StockRestoreDto;
import com.zamazor.market.modules.product.exception.OrderCancellationException;
import com.zamazor.market.modules.product.exception.OrderRefundException;
import com.zamazor.market.modules.user.models.entity.User;
import com.zamazor.market.shared.model.dto.PricingInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@Column(nullable = false)
	private BigDecimal subtotal;

	@Column(nullable = false)
	private BigDecimal tax = BigDecimal.ZERO;

	@Column(nullable = false)
	private BigDecimal shippingCost;

	@Column(nullable = false)
	private BigDecimal discount;

	@Column(nullable = false)
	private BigDecimal total;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@BatchSize(size = 50)
	private List<OrderItem> items = new ArrayList<>();

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
			@AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
			@AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
			@AttributeOverride(name = "phone", column = @Column(name = "recipient_phone"))
	})
	private AddressComponent shippingAddressSnapshot;

	@CreatedDate
	private Instant createdAt;

	public void addOrderItem(OrderItem orderItem) {
		this.items.add(orderItem);
		orderItem.setOrder(this);
	}

	public static Order createFromCart(Cart cart, PricingInfo pricing) {
		var order = new Order();
		order.setUser(cart.getUser());
		order.setStatus(OrderStatus.PENDING);
		order.setSubtotal(pricing.subtotal());
		order.setDiscount(pricing.discount());
		order.setTax(pricing.tax());
		order.setShippingCost(pricing.shipping());
		order.setTotal(pricing.total());

		for (CartItem item : cart.getItems()) {
			var product = item.getProduct();
			product.deductStock(item.getQuantity());

			var newItem = new OrderItem();
			newItem.setOrder(order);
			newItem.setProductId(product.getId());
			newItem.setProductName(product.getName());
			newItem.setProductImageUrl(product.getImageUrl());
			newItem.setUnitPrice(product.getPrice());
			newItem.setQuantity(item.getQuantity());

			order.addOrderItem(newItem);
		}

		return order;
	}

	public List<StockRestoreDto> cancel(LocalDateTime now) {
		if (this.status == OrderStatus.CANCELED) return List.of();
		if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.REFUNDED) {
			throw new OrderCancellationException("Cannot cancel an order that is already delivered or refunded.");
		}
		if (this.createdAt.isBefore(Instant.from(now.minusDays(2).atZone(ZoneOffset.UTC).toInstant()))) {
			throw new OrderCancellationException("Order cancellation window (2 days) has expired.");
		}

		this.status = OrderStatus.CANCELED;
		return getStockRestoreDtos();
	}

	public List<StockRestoreDto> refund(LocalDateTime now) {
		if (this.status == OrderStatus.REFUNDED) {
			return List.of();
		}
		if (this.status != OrderStatus.DELIVERED) {
			throw new OrderRefundException("Only completed or delivered orders can be refunded.");
		}
		if (this.createdAt.isBefore(Instant.from(now.minusDays(2).atZone(ZoneOffset.UTC).toInstant()))) {
			throw new OrderRefundException("Order exceeds the 30-day refund policy window.");
		}

		this.status = OrderStatus.REFUNDED;
		return getStockRestoreDtos();
	}

	public void transitionTo(OrderStatus newStatus) {
		if (this.status == OrderStatus.CANCELED || this.status == OrderStatus.REFUNDED) {
			throw new IllegalOrderStateException("Cannot modify a finalized order");
		}
		this.status = newStatus;
	}

	private List<StockRestoreDto> getStockRestoreDtos() {
		return this.items.stream()
				.map(item -> new StockRestoreDto(item.getProductId(), item.getQuantity()))
				.toList();
	}
}