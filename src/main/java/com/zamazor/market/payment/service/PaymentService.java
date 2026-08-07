package com.zamazor.market.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.zamazor.market.config.ApplicationProperties;
import com.zamazor.market.modules.catalog.models.entity.Order;
import com.zamazor.market.payment.exception.PaymentGatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
	private final ApplicationProperties application;

	public Session createCheckoutSession(Order order) {
		String successUrl = UriComponentsBuilder
				.fromUriString(application.frontendUrl())
				.pathSegment("checkout", "orders", order.getId().toString(), "success")
				.queryParam("sessionId", "{CHECKOUT_SESSION_ID}")
				.build(false)
				.toUriString();

		String cancelUrl = UriComponentsBuilder
				.fromUriString(application.frontendUrl())
				.pathSegment("checkout", "orders", order.getId().toString(), "cancel")
				.build()
				.toUriString();

		SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(successUrl)
				.setCancelUrl(cancelUrl)
				.setCustomerEmail(order.getUser().getEmail())
				.setClientReferenceId(order.getId().toString());

		order.getItems().forEach(item -> sessionBuilder.addLineItem(
				SessionCreateParams.LineItem.builder()
						.setQuantity((long) item.getQuantity())
						.setPriceData(
								SessionCreateParams.LineItem.PriceData.builder()
										.setCurrency("mad")
										.setUnitAmount(item.getUnitPrice().movePointRight(2).longValue())
										.setProductData(
												SessionCreateParams.LineItem.PriceData.ProductData.builder()
														.setName(item.getProductName())
														.build()
										)
										.build()
						)
						.build()
		));

		SessionCreateParams params = sessionBuilder.build();

		try {
			Session session = Session.create(params);
			log.info("Successfully created Stripe checkout session for order: {}", order.getId());
			return session;
		} catch (StripeException e) {
			log.error("Stripe API communication failure for order ID: {}. Error: {}", order.getId(), e.getMessage(), e);
			throw new PaymentGatewayException("Could not initiate payment gateway session. Please try again later.");
		}
	}

	public boolean isSessionPaid(String stripeSessionId) {
		try {
			Session session = Session.retrieve(stripeSessionId);
			return "complete".equals(session.getStatus()) && "paid".equals(session.getPaymentStatus());

		} catch (StripeException e) {
			log.error("Failed to verify session status with Stripe API for Session ID: {}", stripeSessionId, e);
			throw new PaymentGatewayException("Could not verify payment with processor. Please try again.");
		}
	}
}
