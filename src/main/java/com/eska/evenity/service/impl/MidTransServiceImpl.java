package com.eska.evenity.service.impl;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MidTransServiceImpl {
    @Value("${midtrans.server.key}")
    private String serverKey;

    @PostConstruct
    public void init() {
        // Configure Midtrans
        Midtrans.serverKey = serverKey; // Replace with your actual server key
        Midtrans.isProduction = false; // Use `true` for production environment
    }

    public String createTransactionToken(String orderId, long amount) throws MidtransError {
//        Midtrans.isProduction = false; // Use `true` for production environment
        Map<String, Object> params = new HashMap<>();

        // Transaction details
        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", String.valueOf(amount));

        // Payment type configuration
        params.put("transaction_details", transactionDetails);
        params.put("payment_type", "bank_transfer"); // Default to bank transfer

        // Configure bank transfer options
        Map<String, Object> bankTransferOptions = configureBankTransferOptions();
        params.put("bank_transfer", bankTransferOptions);

        // Configure e-wallet options (add your desired e-wallet configurations)
        Map<String, Object> eWalletOptions = configureEWalletOptions();
        params.put("payment_type", "e-wallet");
        params.put("e_wallet", eWalletOptions);

        // Get transaction token
        return SnapApi.createTransactionToken(params);
    }

    private Map<String, Object> configureBankTransferOptions() {
        Map<String, Object> bankTransferOptions = new HashMap<>();

        // BCA Virtual Account
        Map<String, String> bcaOptions = new HashMap<>();
        bcaOptions.put("bank", "bca");
        bankTransferOptions.put("bca", bcaOptions);

        // BNI Virtual Account
        Map<String, String> bniOptions = new HashMap<>();
        bniOptions.put("bank", "bni");
        bankTransferOptions.put("bni", bniOptions);

        // Permata Virtual Account with custom VA number
        Map<String, Object> mandiriOptions = new HashMap<>();
        mandiriOptions.put("bank", "mandiri");
        bankTransferOptions.put("mandiri", mandiriOptions);

        return bankTransferOptions;
    }

    private Map<String, Object> configureEWalletOptions() {
        Map<String, Object> eWalletOptions = new HashMap<>();

        // GoPay
        Map<String, String> gopayOptions = new HashMap<>();
        gopayOptions.put("type", "gopay");
        eWalletOptions.put("gopay", gopayOptions);

        // OVO
        Map<String, String> ovoOptions = new HashMap<>();
        ovoOptions.put("type", "ovo");
        eWalletOptions.put("ovo", ovoOptions);

        // DANA
        Map<String, String> danaOptions = new HashMap<>();
        danaOptions.put("type", "dana");
        eWalletOptions.put("dana", danaOptions);

        return eWalletOptions;
    }
}
