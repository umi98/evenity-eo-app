package com.eska.evenity.controller;

import com.eska.evenity.dto.request.MoneyOnlyRequest;
import com.eska.evenity.dto.response.*;
import com.eska.evenity.service.EventService;
import com.eska.evenity.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final EventService eventService;

    @GetMapping("/detail/event/{id}")
    public ResponseEntity<?> getDetailFromEventId(@PathVariable String id) {
        try {
            TransactionDetail detail = eventService.getTransactionByInvoiceId(id);
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(detail)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getAllBalanceAccout() {
        try {
            List<BalanceResponse> balanceResponses = transactionService.getAllBalanceAccount();
            WebResponse<List<BalanceResponse>> response = WebResponse.<List<BalanceResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(balanceResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/balance/active")
    public ResponseEntity<?> getAllActiveUserBalanceAccount() {
        try {
            List<BalanceResponse> balanceResponses = transactionService.getAllActiveUserBalance();
            WebResponse<List<BalanceResponse>> response = WebResponse.<List<BalanceResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(balanceResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getAllTransactionHistory() {
        try {
            List<TransactionHistoryResponse> historyResponses = transactionService.getAllTransactionHistory();
            WebResponse<List<TransactionHistoryResponse>> response = WebResponse.<List<TransactionHistoryResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(historyResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getAllTransactionHistoryByUserId(@PathVariable String userId) {
        try {
            List<TransactionHistoryResponse> historyResponses = transactionService.getAllTransactionHistoryByUserId(userId);
            WebResponse<List<TransactionHistoryResponse>> response = WebResponse.<List<TransactionHistoryResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(historyResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/balance/user/{id}")
    public ResponseEntity<?> createBalance(@PathVariable String id) {
        try {
            BalanceResponse balanceResponses = transactionService.createBalance(id);
            WebResponse<BalanceResponse> response = WebResponse.<BalanceResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(balanceResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/balance/{id}")
    public ResponseEntity<?> getBalanceById(@PathVariable String id) {
        try {
            BalanceResponse balanceResponses = transactionService.getBalanceById(id);
            WebResponse<BalanceResponse> response = WebResponse.<BalanceResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(balanceResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/balance/user/{id}")
    public ResponseEntity<?> getBalanceByUserId(@PathVariable String id) {
        try {
            BalanceResponse balanceResponses = transactionService.getBalanceByUserId(id);
            WebResponse<BalanceResponse> response = WebResponse.<BalanceResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(balanceResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/withdraw/request")
    public ResponseEntity<?> getAllWithdrawRequest() {
        try {
            List<WithdrawRequestResponse> withdrawResponse = transactionService.getAllWithdrawRequest();
            WebResponse<List<WithdrawRequestResponse>> response = WebResponse.<List<WithdrawRequestResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(withdrawResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/withdraw/request/user/{id}")
    public ResponseEntity<?> getAllWithdrawRequestByUserId(@PathVariable String id) {
        try {
            List<WithdrawRequestResponse> withdrawResponse = transactionService.getAllWithdrawRequestByUserId(id);
            WebResponse<List<WithdrawRequestResponse>> response = WebResponse.<List<WithdrawRequestResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(withdrawResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/withdraw/balance/{id}")
    public ResponseEntity<?> withdrawRequest(@PathVariable String id, @Valid @RequestBody MoneyOnlyRequest request) {
        try {
            WithdrawRequestResponse withdrawResponse = transactionService.withDrawRequest(id, request);
            WebResponse<WithdrawRequestResponse> response = WebResponse.<WithdrawRequestResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(withdrawResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/withdraw/{id}/approve")
    public ResponseEntity<?> approveWithdraw(@PathVariable String id) {
        try {
            WithdrawRequestResponse withdrawResponses = transactionService.approveWithdrawRequest(id);
            WebResponse<WithdrawRequestResponse> response = WebResponse.<WithdrawRequestResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(withdrawResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/withdraw/{id}/reject")
    public ResponseEntity<?> rejectWithdraw(@PathVariable String id) {
        try {
            WithdrawRequestResponse withdrawResponses = transactionService.rejectWithdrawRequest(id);
            WebResponse<WithdrawRequestResponse> response = WebResponse.<WithdrawRequestResponse>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(withdrawResponses)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
