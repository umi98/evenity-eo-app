package com.eska.evenity.service;

import java.util.List;

import com.eska.evenity.dto.request.MoneyOnlyRequest;
import com.eska.evenity.dto.response.BalanceResponse;
import com.eska.evenity.dto.response.TransactionHistoryResponse;
import com.eska.evenity.dto.response.WithdrawRequestResponse;
import com.eska.evenity.entity.Balance;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Invoice;

public interface TransactionService {
    List<BalanceResponse> getAllBalanceAccount();
    List<BalanceResponse> getAllActiveUserBalance();
    BalanceResponse createBalance(String userId);
    BalanceResponse getBalanceById(String id);
    BalanceResponse getBalanceByUserId(String userId);
    Balance getBalanceUsingUserId(String userId);
    List<WithdrawRequestResponse> getAllWithdrawRequest();
    List<WithdrawRequestResponse> getAllWithdrawRequestByUserId(String id);
    WithdrawRequestResponse withDrawRequest(String balanceId, MoneyOnlyRequest request);
    WithdrawRequestResponse approveWithdrawRequest(String requestId);
    WithdrawRequestResponse rejectWithdrawRequest(String requestId);
    List<TransactionHistoryResponse> getAllTransactionHistory();
    List<TransactionHistoryResponse> getAllTransactionHistoryByUserId(String userId);
    void changeBalanceWhenCustomerPay(Long amount, Event event);
    void changeBalanceWhenTransfer(Long amount, EventDetail eventDetail);
}
