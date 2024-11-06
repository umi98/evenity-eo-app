package com.eska.evenity.service;

import java.util.List;

import com.eska.evenity.dto.request.MoneyOnlyRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.BalanceResponse;
import com.eska.evenity.dto.response.TransactionHistoryResponse;
import com.eska.evenity.dto.response.WithdrawRequestResponse;
import com.eska.evenity.entity.Balance;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Invoice;
import org.springframework.data.domain.Page;

public interface TransactionService {
    Page<BalanceResponse> getAllBalanceAccount(PagingRequest pagingRequest);
    Page<BalanceResponse> getAllActiveUserBalance(PagingRequest pagingRequest);
    BalanceResponse createBalance(String userId);
    BalanceResponse getBalanceById(String id);
    BalanceResponse getBalanceByUserId(String userId);
    Balance getBalanceUsingUserId(String userId);
    Page<WithdrawRequestResponse> getAllWithdrawRequest(PagingRequest pagingRequest);
    Page<WithdrawRequestResponse> getAllWithdrawRequestByUserId(String id, PagingRequest pagingRequest);
    WithdrawRequestResponse withDrawRequest(String userId, MoneyOnlyRequest request);
    WithdrawRequestResponse approveWithdrawRequest(String requestId);
    WithdrawRequestResponse rejectWithdrawRequest(String requestId);
    Page<TransactionHistoryResponse> getAllTransactionHistory(PagingRequest pagingRequest);
    Page<TransactionHistoryResponse> getAllTransactionHistoryByUserId(String userId, PagingRequest pagingRequest);
    void changeBalanceWhenCustomerPay(Long amount, Event event);
    void changeBalanceWhenTransfer(Long amount, EventDetail eventDetail);
}
