package com.zamazor.market.modules.catalog.specification;

import com.zamazor.market.modules.catalog.models.entity.Order;
import com.zamazor.market.modules.catalog.models.entity.OrderStatus;
import com.zamazor.market.modules.user.models.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecifications {

	public static Specification<Order> createSpec(String userFullName, OrderStatus orderStatus) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// 1. Structural join for filtering paths
			Join<Order, User> userJoin = root.join("user", JoinType.LEFT);

			// 2. Fetch User ONLY (Safe for pagination as it's a To-One relationship)
			if (query.getResultType() != Long.class && query.getResultType() != long.class) {
				root.fetch("user", JoinType.LEFT);
			}

			// 3. Filters
			if (userFullName != null && !userFullName.isBlank()) {
				String pattern = "%" + userFullName.toLowerCase() + "%";
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(userJoin.get("fullName")), pattern));
			}

			if (orderStatus != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), orderStatus));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}