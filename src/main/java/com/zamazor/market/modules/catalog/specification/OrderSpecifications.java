package com.zamazor.market.modules.catalog.specification;

import com.zamazor.market.modules.catalog.models.entity.Order;
import com.zamazor.market.modules.catalog.models.entity.OrderStatus;
import com.zamazor.market.modules.user.models.entity.User;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecifications {

	@Contract(pure = true)
	public static @NonNull Specification<Order> createSpec(String userFullName, OrderStatus orderStatus) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (userFullName != null && !userFullName.isBlank()) {
				String pattern = "%" + userFullName.toLowerCase() + "%";

				Join<Order, User> userJoin = root.join("user", JoinType.LEFT);

				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("fullName")), pattern));
			}

			if (orderStatus != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), orderStatus));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}