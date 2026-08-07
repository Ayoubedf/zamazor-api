package com.zamazor.market.mail.listener;

import com.zamazor.market.config.ApplicationProperties;
import com.zamazor.market.mail.event.OrderPlacedEvent;
import com.zamazor.market.mail.event.OrderStatusChangedEvent;
import com.zamazor.market.mail.service.EmailService;
import com.zamazor.market.modules.catalog.models.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEmailListener {
	private final EmailService emailService;
	private final ApplicationProperties application;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderPlacedNotification(OrderPlacedEvent event) {
		var order = event.order();
		var user = event.user();

		emailService.sendHtmlEmail(
				user.getEmail(),
				"Action Required: Complete your order #" + order.getId(),
				"checkout-success",
				buildCheckoutEmailVariables(order)
		);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderNotifications(OrderStatusChangedEvent event) {
		String userEmail = event.user().getEmail();
		var order = event.order();
		String orderIdStr = order.getId().toString();

		Map<String, Object> baseModel = Map.of(
				"appName", application.name(),
				"orderId", orderIdStr,
				"supportEmail", application.supportEmail(),
				"year", Year.now().getValue()
		);

		Map<String, Object> paidModel = new HashMap<>(baseModel);
		paidModel.put("items", order.getItems());
		paidModel.put("totalAmount", order.getTotal() + "MAD");

		switch (event.status()) {
			case PAID -> emailService.sendHtmlEmail(
					userEmail,
					"Payment Confirmed - Order #" + orderIdStr,
					"order-success-receipt",
					paidModel
			);
			case CANCELED -> emailService.sendHtmlEmail(
					userEmail,
					"Your Order #" + orderIdStr + " has been cancelled",
					"order-canceled",
					baseModel
			);
			case REFUNDED -> emailService.sendHtmlEmail(
					userEmail,
					"Refund Confirmed for Order #" + orderIdStr,
					"order-refunded",
					baseModel
			);
			default -> log.debug("No notification email needed for order status: {}", event.status());
		}
	}

	private Map<String, Object> buildCheckoutEmailVariables(Order order) {
		return Map.of(
				"appName", application.name(),
				"paymentUrl", "%s/orders/%s/pay"
						.formatted(application.backendUrl(), order.getId()),
				"orderId", order.getId(),
				"items", order.getItems().stream().toList(),
				"totalAmount", "MAD %s".formatted(order.getTotal()),
				"supportEmail", application.supportEmail(),
				"year", Year.now().getValue()
		);
	}
}