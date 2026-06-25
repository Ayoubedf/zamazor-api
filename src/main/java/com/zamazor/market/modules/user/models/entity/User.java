package com.zamazor.market.modules.user.models.entity;

import com.zamazor.market.modules.catalog.models.entity.Cart;
import com.zamazor.market.modules.catalog.models.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NullMarked
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "full_name")
	private String fullName;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "is_admin", nullable = false)
	private Boolean isAdmin;

	@Column(nullable = false)
	private String password;

	public Role getRole() {
		return isAdmin ? Role.ADMIN : Role.USER;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(getRole());
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id")
	private Cart cart;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Order> orders;
}
