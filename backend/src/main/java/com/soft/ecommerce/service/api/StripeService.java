package com.soft.ecommerce.service.api;

import com.soft.ecommerce.payload.StripePaymentDTO;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO);
}
