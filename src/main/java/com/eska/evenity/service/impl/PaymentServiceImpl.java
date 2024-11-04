package com.eska.evenity.service.impl;

import com.eska.evenity.dto.request.PaymentDetailRequest;
import com.eska.evenity.dto.request.PaymentRequest;
import com.eska.evenity.entity.Payment;
import com.eska.evenity.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {
    private final PaymentRepository paymentRepository;
    private final RestClient restClient;

    @Value("${midtrans.server.key}")
    private String SECRET_KEY;

    @Value("${midtrans.snap.url}")
    private String BASE_SNAP_URL;

    public Payment create(String invoiceId, Long amount) {
        String username = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
        PaymentDetailRequest paymentDetailRequest = PaymentDetailRequest.builder()
                .orderId(invoiceId)
                .amount(amount)
                .build();
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentDetailRequest(paymentDetailRequest)
                .paymentMethod(List.of(
                        "shopeepay",
                        "gopay",
                        "indomaret"
                ))
                .build();
        ResponseEntity<Map<String,String>> response = restClient.post()
                .uri(BASE_SNAP_URL)
                .body(paymentRequest)
                .header(HttpHeaders.AUTHORIZATION,"Basic " + username)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        Map<String,String> body = response.getBody();
        Payment payment = Payment.builder()
                .token(body.get("token"))
                .redirectUrl(body.get("redirect_url"))
                .transactionStatus("ordered")
                .build();
        paymentRepository.saveAndFlush(payment);
        return payment;
    }
}
