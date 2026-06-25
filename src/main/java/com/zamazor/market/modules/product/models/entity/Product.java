package com.zamazor.market.modules.product.models.entity;

import com.zamazor.market.modules.catalog.exception.OutOfStockException;
import com.zamazor.market.modules.catalog.models.entity.CartItem;
import com.zamazor.market.modules.catalog.models.entity.OrderItem;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"store", "category"})
@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal price;

	@Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
	private String imageUrl;

	@Builder.Default
	@Column(name = "stock_quantity", nullable = false)
	private Integer stockQuantity = 0;

	@Builder.Default
	@Column(name = "reserved_quantity", nullable = false)
	private Integer reservedQuantity = 0;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "store_id",  nullable = false)
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY,  optional = false)
	@JoinColumn(name = "category_id",  nullable = false)
	private Category category;

	@Builder.Default
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<OrderItem> orderItems = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<CartItem> cartItems = new ArrayList<>();

	@Version
	@Builder.Default
	@Column(name = "version", nullable = false)
	private Long version = 0L;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	public void deductStock(int quantity) {
		if (this.stockQuantity < quantity) {
			throw new OutOfStockException("Not enough stock for: " + this.name);
		}
		this.stockQuantity -= quantity;
	}

	public void restoreStock(Integer quantity) {
		if (quantity == null || quantity <= 0) {
			throw new IllegalArgumentException("Quantity to restore must be greater than zero");
		}
		this.stockQuantity += quantity;
	}
}
