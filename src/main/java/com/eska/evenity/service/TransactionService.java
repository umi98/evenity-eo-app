package com.eska.evenity.service;

import com.eska.evenity.dto.request.MoneyOnlyRequest;
import com.eska.evenity.dto.request.TransferRequest;
import com.eska.evenity.dto.response.BalanceResponse;
import com.eska.evenity.dto.response.TransactionHistoryResponse;
import com.eska.evenity.dto.response.WithdrawRequestResponse;
import com.eska.evenity.entity.Balance;
import com.eska.evenity.entity.TransactionHistory;

import java.util.List;

public interface TransactionService {
    List<BalanceResponse> getAllBalanceAccount();
    List<BalanceResponse> getAllActiveUserBalance();
    BalanceResponse createBalance(String userId);
    BalanceResponse getBalanceById(String id);
    BalanceResponse getBalanceByUserId(String userId);
    List<WithdrawRequestResponse> getAllWithdrawRequest();
    List<WithdrawRequestResponse> getAllWithdrawRequestByUserId(String id);
    WithdrawRequestResponse withDrawRequest(String balanceId, MoneyOnlyRequest request);
    WithdrawRequestResponse approveWithdrawRequest(String requestId);
    WithdrawRequestResponse rejectWithdrawRequest(String requestId);
    List<TransactionHistoryResponse> getAllTransactionHistory();
    List<TransactionHistoryResponse> getAllTransactionHistoryByUserId(String userId);
}