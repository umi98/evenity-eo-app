package com.eska.evenity.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.TransactionType;
import com.eska.evenity.constant.UserStatus;
import com.eska.evenity.dto.request.MoneyOnlyRequest;
import com.eska.evenity.dto.response.BalanceResponse;
import com.eska.evenity.dto.response.TransactionHistoryResponse;
import com.eska.evenity.dto.response.WithdrawRequestResponse;
import com.eska.evenity.entity.Balance;
import com.eska.evenity.entity.TransactionHistory;
import com.eska.evenity.entity.UserCredential;
import com.eska.evenity.entity.WithdrawRequest;
import com.eska.evenity.repository.BalanceRepository;
import com.eska.evenity.repository.TransactionHistoryRepository;
import com.eska.evenity.repository.WithdrawRequestRepository;
import com.eska.evenity.service.TransactionService;
import com.eska.evenity.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final BalanceRepository balanceRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final WithdrawRequestRepository withdrawRequestRepository;
    private final UserService userService;

    @Override
    public List<BalanceResponse> getAllBalanceAccount() {
        List<Balance> result = balanceRepository.findAll();
        return result.stream().map(this::mapBalanceToResponse).toList();
    }

    @Override
    public List<BalanceResponse> getAllActiveUserBalance() {
        List<Balance> result = balanceRepository.findBalanceByUserCredential_Status(UserStatus.ACTIVE);
        return result.stream().map(this::mapBalanceToResponse).toList();
    }

    @Override
    public BalanceResponse createBalance(String userId) {
        UserCredential user = userService.loadByUserId(userId);
        Balance balance = Balance.builder()
                .amount(0L)
                .userCredential(user)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .build();
        balanceRepository.saveAndFlush(balance);
        TransactionHistory history = TransactionHistory.builder()
                .amount(0L)
                .activity(TransactionType.OPEN)
                .description("User id " + user.getId() + " (" + user.getUsername() + ") open balance account.")
                .createdDate(LocalDateTime.now())
                .createdBy(user)
                .build();
        transactionHistoryRepository.saveAndFlush(history);
        return mapBalanceToResponse(balance);
    }

    @Override
    public BalanceResponse getBalanceById(String id) {
        Balance balance = balanceRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "balance not found")
        );
        return mapBalanceToResponse(balance);
    }

    @Override
    public BalanceResponse getBalanceByUserId(String userId) {
        Balance balance = findBalanceByUserIdOrThrowException(userId);
        return mapBalanceToResponse(balance);
    }

    @Override
    public List<WithdrawRequestResponse> getAllWithdrawRequest() {
        List<WithdrawRequest> result = withdrawRequestRepository.findAll();
        return result.stream().map(this::mapRequestToResponse).toList();
    }

    @Override
    public List<WithdrawRequestResponse> getAllWithdrawRequestByUserId(String id) {
        List<WithdrawRequest> result = withdrawRequestRepository.findAllByBalance_UserCredential_Id(id);
        return result.stream().map(this::mapRequestToResponse).toList();
    }

    @Override
    public WithdrawRequestResponse withDrawRequest(String balanceId, MoneyOnlyRequest request) {
        Balance balance = findBalanceByIdOrThrowException(balanceId);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .amount(request.getAmount())
                .approvalStatus(ApprovalStatus.PENDING)
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .balance(balance)
                .build();
        withdrawRequestRepository.saveAndFlush(withdrawRequest);
        UserCredential user = userService.loadByUserId(balance.getUserCredential().getId());
        TransactionHistory history = TransactionHistory.builder()
                .amount(0L)
                .activity(TransactionType.WITHDRAW_REQUEST)
                .description("User id " + user.getId() + " (" + user.getUsername() +
                        ") request for withdrawal: IDR " + request.getAmount() +
                        " with request code: " + withdrawRequest.getId())
                .createdDate(LocalDateTime.now())
                .createdBy(user)
                .build();
        transactionHistoryRepository.saveAndFlush(history);
        return mapRequestToResponse(withdrawRequest);
    }

    @Override
    public WithdrawRequestResponse approveWithdrawRequest(String requestId) {
        WithdrawRequest request = findRequestByIdOrThrowException(requestId);
        request.setApprovalStatus(ApprovalStatus.APPROVED);
        request.setModifiedDate(LocalDateTime.now());
        withdrawRequestRepository.saveAndFlush(request);
        Balance balance = findBalanceByIdOrThrowException(request.getBalance().getId());
        balance.setAmount(balance.getAmount() - request.getAmount());
        balance.setModifiedDate(LocalDateTime.now());
        balanceRepository.saveAndFlush(balance);
        UserCredential user = userService.loadByUserId(balance.getUserCredential().getId());
        TransactionHistory history = TransactionHistory.builder()
                .amount(request.getAmount())
                .activity(TransactionType.WITHDRAW)
                .description("Admin approved User id " + user.getId() + " (" + user.getUsername() +
                        ") to withdraw: IDR " + request.getAmount() +
                        " with request code: " + request.getId())
                .createdDate(LocalDateTime.now())
                .createdBy(user)
                .build();
        transactionHistoryRepository.saveAndFlush(history);
        return mapRequestToResponse(request);
    }

    @Override
    public WithdrawRequestResponse rejectWithdrawRequest(String requestId) {
        WithdrawRequest request = findRequestByIdOrThrowException(requestId);
        request.setApprovalStatus(ApprovalStatus.REJECTED);
        request.setModifiedDate(LocalDateTime.now());
        withdrawRequestRepository.saveAndFlush(request);
        Balance balance = findBalanceByIdOrThrowException(request.getBalance().getId());
        UserCredential user = userService.loadByUserId(balance.getUserCredential().getId());
        TransactionHistory history = TransactionHistory.builder()
                .amount(0L)
                .activity(TransactionType.WITHDRAW_REQUEST)
                .description("Admin rejected User id " + user.getId() + " (" + user.getUsername() +
                        ") to withdraw: IDR " + request.getAmount() +
                        " from request code: " + request.getId())
                .createdDate(LocalDateTime.now())
                .createdBy(user)
                .build();
        transactionHistoryRepository.saveAndFlush(history);
        return mapRequestToResponse(request);
    }

    @Override
    public List<TransactionHistoryResponse> getAllTransactionHistory() {
        List<TransactionHistory> result = transactionHistoryRepository.findAll();
        return result.stream().map(this::mapHistoryToResponse).toList();
    }

    @Override
    public List<TransactionHistoryResponse> getAllTransactionHistoryByUserId(String userId) {
        List<TransactionHistory> result = transactionHistoryRepository.getTransactionHistoryByCreatedBy_Id(userId);
        return result.stream().map(this::mapHistoryToResponse).toList();
    }

    private Balance findBalanceByUserIdOrThrowException(String id) {
        return balanceRepository.findBalanceByUserCredential_Id(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "balance not found")
        );
    }

    private Balance findBalanceByIdOrThrowException(String id) {
        return balanceRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "balance not found")
        );
    }

    private WithdrawRequest findRequestByIdOrThrowException(String id) {
        return withdrawRequestRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "request not found")
        );
    }

    private BalanceResponse mapBalanceToResponse(Balance balance) {
        return BalanceResponse.builder()
                .id(balance.getId())
                .amount(balance.getAmount())
                .userId(balance.getUserCredential().getId())
                .userName(balance.getUserCredential().getUsername())
                .modifiedDate(balance.getModifiedDate())
                .createdDate(balance.getCreatedDate())
                .build();
    }

    private TransactionHistoryResponse mapHistoryToResponse(TransactionHistory transactionHistory) {
        return TransactionHistoryResponse.builder()
                .id(transactionHistory.getId())
                .activity(transactionHistory.getActivity().name())
                .amount(transactionHistory.getAmount())
                .description(transactionHistory.getDescription())
                .createdDate(transactionHistory.getCreatedDate())
                .createdBy(transactionHistory.getCreatedBy().getId())
                .createdByName(transactionHistory.getCreatedBy().getUsername())
                .build();
    }

    private WithdrawRequestResponse mapRequestToResponse(WithdrawRequest withdrawRequest) {
        return WithdrawRequestResponse.builder()
                .id(withdrawRequest.getId())
                .amount(withdrawRequest.getAmount())
                .approvalStatus(withdrawRequest.getApprovalStatus().name())
                .balanceId(withdrawRequest.getBalance().getId())
                .userName(withdrawRequest.getBalance().getUserCredential().getUsername())
                .createdDate(withdrawRequest.getCreatedDate())
                .modifiedDate(withdrawRequest.getModifiedDate())
                .build();
    }
}
