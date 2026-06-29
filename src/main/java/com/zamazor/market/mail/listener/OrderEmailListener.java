package com.zamazor.market.mail.listener;

import com.zamazor.market.config.ApplicationProperties;
import com.zamazor.market.mail.event.OrderStatusChangedEvent;
import com.zamazor.market.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Year;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEmailListener {
	private final EmailService emailService;
	private final ApplicationProperties application;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleOrderNotifications(OrderStatusChangedEvent event) {
		String userEmail = event.user().getEmail();
		String orderIdStr = event.order().getId().toString();

		Map<String, Object> baseModel = Map.of(
				"appName", application.name(),
				"orderId", orderIdStr,
				"supportEmail", application.supportEmail(),
				"year", Year.now().getValue()
		);

		switch (event.status()) {
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
			default -> log.info("No email configured for status: {}", event.status());
		}
	}
}