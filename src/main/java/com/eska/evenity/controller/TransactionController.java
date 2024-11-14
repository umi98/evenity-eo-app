package com.eska.evenity.controller;

import com.eska.evenity.dto.request.MoneyOnlyRequest;
import com.eska.evenity.dto.request.PagingRequest;
import com.eska.evenity.dto.response.*;
import com.eska.evenity.service.EventService;
import com.eska.evenity.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {
    private final TransactionService transactionService;
    private final EventService eventService;

    @GetMapping("/detail/invoice/{id}")
    public ResponseEntity<?> getDetailFromInvoiceId(@PathVariable String id) {
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
    public ResponseEntity<?> getAllBalanceAccount(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<BalanceResponse> balanceResponses = transactionService.getAllBalanceAccount(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(balanceResponses.getTotalElements())
                    .totalPage(balanceResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(balanceResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/balance/active")
    public ResponseEntity<?> getAllActiveUserBalanceAccount(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<BalanceResponse> balanceResponses = transactionService.getAllActiveUserBalance(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(balanceResponses.getTotalElements())
                    .totalPage(balanceResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(balanceResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getAllTransactionHistory(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<TransactionHistoryResponse> historyResponses = transactionService.getAllTransactionHistory(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(historyResponses.getTotalElements())
                    .totalPage(historyResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(historyResponses.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getAllTransactionHistoryByUserId(
            @PathVariable String userId,
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<TransactionHistoryResponse> historyResponses = transactionService.getAllTransactionHistoryByUserId(userId, pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(historyResponses.getTotalElements())
                    .totalPage(historyResponses.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(historyResponses.getContent())
                    .pagingResponse(pagingResponse)
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
    public ResponseEntity<?> getAllWithdrawRequest(
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<WithdrawRequestResponse> withdrawResponse = transactionService.getAllWithdrawRequest(pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(withdrawResponse.getTotalElements())
                    .totalPage(withdrawResponse.getTotalPages())
                    .build();
            WebResponse<?> response = WebResponse.builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(withdrawResponse.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/withdraw/request/user/{id}")
    public ResponseEntity<?> getAllWithdrawRequestByUserId(
            @PathVariable String id,
            @RequestParam (required = false, defaultValue = "1") Integer page,
            @RequestParam (required = false, defaultValue = "100") Integer size
    ) {
        try {
            PagingRequest pagingRequest = PagingRequest.builder()
                    .page(page)
                    .size(size)
                    .build();
            Page<WithdrawRequestResponse> withdrawResponse = transactionService.getAllWithdrawRequestByUserId(id, pagingRequest);
            PagingResponse pagingResponse = PagingResponse.builder()
                    .page(page)
                    .size(size)
                    .count(withdrawResponse.getTotalElements())
                    .totalPage(withdrawResponse.getTotalPages())
                    .build();
            WebResponse<List<WithdrawRequestResponse>> response = WebResponse.<List<WithdrawRequestResponse>>builder()
                    .status(HttpStatus.OK.getReasonPhrase())
                    .message("Successfully retrieve data")
                    .data(withdrawResponse.getContent())
                    .pagingResponse(pagingResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/withdraw/{userId}")
    public ResponseEntity<?> withdrawRequest(@PathVariable String userId, @Valid @RequestBody MoneyOnlyRequest request) {
        try {
            WithdrawRequestResponse withdrawResponse = transactionService.withDrawRequest(userId, request);
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
    public ResponseEntity<?> approveWithdraw(
            @PathVariable String id,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            if (image.isEmpty()) throw new IllegalArgumentException("image is empty");
            WithdrawRequestResponse withdrawResponses = transactionService.approveWithdrawRequest(id, image);
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
