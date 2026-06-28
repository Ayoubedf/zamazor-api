package com.zamazor.market.modules.product.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "products")
@Entity
@Table(name = "stores")
public class Store {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(unique = true, length = 20)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "logo_url", columnDefinition = "TEXT")
	private String logoUrl;

	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> products;
}
