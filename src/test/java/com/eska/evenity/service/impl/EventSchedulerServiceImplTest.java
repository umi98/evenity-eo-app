package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.constant.PaymentStatus;
import com.eska.evenity.constant.ProductUnit;
import com.eska.evenity.entity.*;
import com.eska.evenity.repository.BalanceRepository;
import com.eska.evenity.repository.EventDetailRepository;
import com.eska.evenity.repository.EventRepository;
import com.eska.evenity.repository.InvoiceDetailRepository;
import com.eska.evenity.service.TransactionService;
import com.eska.evenity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EventSchedulerServiceImplTest {
    @Spy
    @InjectMocks
    private EventSchedulerServiceImpl eventSchedulerService;

    @Mock
    private EventDetailRepository eventDetailRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private InvoiceDetailRepository invoiceDetailRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserService userService;

    @Mock
    private BalanceRepository balanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunAsyncTask_Success() {
        // Arrange
        doNothing().when(eventSchedulerService).autoRejectPendingEventDetails();
        doNothing().when(eventSchedulerService).changeProgressionStatus();
        doNothing().when(eventSchedulerService).processBalanceTransfers();

        // Act
        CompletableFuture<Void> future = eventSchedulerService.runAsyncTask();

        // Assert
        assertNotNull(future);
        verify(eventSchedulerService, times(1)).autoRejectPendingEventDetails();
        verify(eventSchedulerService, times(1)).changeProgressionStatus();
        verify(eventSchedulerService, times(1)).processBalanceTransfers();
    }

    @Test
    void testRunAsyncTask_ExceptionHandling() {
        // Arrange
        doThrow(new RuntimeException("Error in autoRejectPendingEventDetails"))
                .when(eventSchedulerService).autoRejectPendingEventDetails();
        doNothing().when(eventSchedulerService).changeProgressionStatus();
        doNothing().when(eventSchedulerService).processBalanceTransfers();

        // Act
        CompletableFuture<Void> future = eventSchedulerService.runAsyncTask();

        // Assert
        assertNotNull(future);
        verify(eventSchedulerService, times(1)).autoRejectPendingEventDetails();
    }

    @Test
    void testAutoRejectPendingEventDetails() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1000);
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        EventDetail eventDetail = new EventDetail();
        eventDetail.setId("1");
        eventDetail.setApprovalStatus(ApprovalStatus.PENDING);
        eventDetail.setModifiedDate(LocalDateTime.now());
        eventDetail.setCreatedDate(LocalDateTime.now());

        Page<EventDetail> pendingEventDetails = new PageImpl<>(Collections.singletonList(eventDetail));

        // Mock the repository call with the correct parameters
        when(eventDetailRepository.findByApprovalStatusAndCreatedDateAfter(
                eq(ApprovalStatus.PENDING),
                argThat(date -> date.isBefore(LocalDateTime.now()) && date.isAfter(twentyFourHoursAgo)), // Use a matcher for flexibility
                eq(pageable)
        ))
                .thenReturn(pendingEventDetails);

        // Act
        eventSchedulerService.autoRejectPendingEventDetails();

        // Assert
        verify(eventDetailRepository, times(1)).findByApprovalStatusAndCreatedDateAfter(
                eq(ApprovalStatus.PENDING),
                argThat(date -> date.isBefore(LocalDateTime.now()) && date.isAfter(twentyFourHoursAgo)), // Match the same condition
                eq(pageable)
        );
        verify(eventDetailRepository, times(1)).saveAndFlush(eventDetail);
        assertEquals(ApprovalStatus.REJECTED, eventDetail.getApprovalStatus());
    }

    @Test
    void testAutoRejectPendingEventDetails_ExceptionHandling() {
        // Arrange
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        // Mock the repository to throw an exception when called
        when(eventDetailRepository.findByApprovalStatusAndCreatedDateAfter(
                eq(ApprovalStatus.PENDING),
                argThat(date -> date.isBefore(LocalDateTime.now()) && date.isAfter(twentyFourHoursAgo)), // Use a matcher for flexibility
                eq(PageRequest.of(0, 1000)) // Match the pageable configuration used in your service
        )).thenThrow(new RuntimeException("Database error"));

        // Act
        try {
            eventSchedulerService.autoRejectPendingEventDetails();
        } catch (RuntimeException e) {
            // Handle the exception if necessary, or just ignore it for this test
        }

        // Assert
        verify(eventDetailRepository, times(1)).findByApprovalStatusAndCreatedDateAfter(
                eq(ApprovalStatus.PENDING),
                argThat(date -> date.isBefore(LocalDateTime.now()) && date.isAfter(twentyFourHoursAgo)), // Use a matcher
                eq(PageRequest.of(0, 1000)) // Ensure this matches the actual pageable used in the service
        );
        verify(eventDetailRepository, never()).saveAndFlush(any(EventDetail.class));
    }

    @Test
    void testChangeProgressionStatus_OngoingEvent() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Event ongoingEvent = new Event();
        ongoingEvent.setId("1");
        ongoingEvent.setStartDate(LocalDate.now());
        ongoingEvent.setStartTime(now.minusHours(1).toLocalTime());
        ongoingEvent.setEndDate(LocalDate.now());
        ongoingEvent.setEndTime(now.plusHours(1).toLocalTime());

        when(eventRepository.findByIsDeleted(false)).thenReturn(Arrays.asList(ongoingEvent));

        // Act
        eventSchedulerService.changeProgressionStatus();

        // Assert
        verify(eventDetailRepository, times(1)).updateEventProgress("1", EventProgress.ON_PROGRESS);
        verify(eventDetailRepository, never()).updateEventProgress("1", EventProgress.FINISHED);
    }

    @Test
    void testChangeProgressionStatus_FinishedEvent() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Event finishedEvent = new Event();
        finishedEvent.setId("2");
        finishedEvent.setStartDate(LocalDate.now());
        finishedEvent.setStartTime(now.minusHours(2).toLocalTime());
        finishedEvent.setEndDate(LocalDate.now());
        finishedEvent.setEndTime(now.minusHours(1).toLocalTime());

        when(eventRepository.findByIsDeleted(false)).thenReturn(Arrays.asList(finishedEvent));

        // Act
        eventSchedulerService.changeProgressionStatus();

        // Assert
        verify(eventDetailRepository, times(1)).updateEventProgress("2", EventProgress.FINISHED);
        verify(eventDetailRepository, never()).updateEventProgress("2", EventProgress.ON_PROGRESS);
    }

    @Test
    void testChangeProgressionStatus_NotStartedEvent() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Event notStartedEvent = new Event();
        notStartedEvent.setId("3");
        notStartedEvent.setStartDate(LocalDate.now());
        notStartedEvent.setStartTime(now.plusHours(1).toLocalTime()); // Starts in the future
        notStartedEvent.setEndDate(LocalDate.now());
        notStartedEvent.setEndTime(now.plusHours(2).toLocalTime());

        when(eventRepository.findByIsDeleted(false)).thenReturn(Arrays.asList(notStartedEvent));

        // Act
        eventSchedulerService.changeProgressionStatus();

        // Assert
        verify(eventDetailRepository, never()).updateEventProgress("3", EventProgress.ON_PROGRESS);
        verify(eventDetailRepository, never()).updateEventProgress("3", EventProgress.FINISHED);
    }

    @Test
    void testChangeProgressionStatus_ExceptionHandling() {
        // Arrange
        when(eventRepository.findByIsDeleted(false)).thenThrow(new RuntimeException("Database error"));

        // Act
        assertDoesNotThrow(() -> eventSchedulerService.changeProgressionStatus());

        // Assert
        // No interactions with eventDetailRepository since an exception occurred
        verify(eventDetailRepository, never()).updateEventProgress(anyString(), any(EventProgress.class));
    }

    @Test
    public void testProcessBalanceTransfers_Success() {
        // Set up test data
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        Vendor vendor = mock(Vendor.class);
        Product product = mock(Product.class);
        InvoiceDetail invoiceDetail = mock(InvoiceDetail.class);
        Invoice invoice = mock(Invoice.class);
        Event event = mock(Event.class);
        EventDetail eventDetail = mock(EventDetail.class);
        UserCredential userCredential = mock(UserCredential.class);
        Balance senderBalance = mock(Balance.class);
        Balance recipientBalance = mock(Balance.class);

        // Mocking the necessary behavior
        when(invoiceDetail.getInvoice()).thenReturn(invoice);
        when(invoice.getEvent()).thenReturn(event);
        when(invoice.getStatus()).thenReturn(PaymentStatus.COMPLETE);
        when(event.getEndDate()).thenReturn(threeDaysAgo.toLocalDate());
        when(event.getEndTime()).thenReturn(LocalTime.now());
        when(invoiceDetail.getStatus()).thenReturn(PaymentStatus.UNPAID);
        when(invoiceDetail.getEventDetail()).thenReturn(eventDetail);
        when(eventDetail.getApprovalStatus()).thenReturn(ApprovalStatus.APPROVED);
        when(eventDetail.getCost()).thenReturn(100L); // Changed to double for cost

        when(eventDetail.getProduct()).thenReturn(product);
        when(product.getVendor()).thenReturn(vendor);
        when(vendor.getUserCredential()).thenReturn(userCredential);

        // Mocking user and balance behavior
        when(userService.findByUsername("admin@gmail.com")).thenReturn(userCredential);
        when(userCredential.getId()).thenReturn("1");
        when(balanceRepository.findBalanceByUserCredential_Id("1")).thenReturn(Optional.of(senderBalance));
        when(eventDetail.getProduct().getVendor().getUserCredential().getId()).thenReturn("1");
        when(balanceRepository.findBalanceByUserCredential_Id("2")).thenReturn(Optional.of(recipientBalance));
        when(senderBalance.getAmount()).thenReturn(1000L);
        when(recipientBalance.getAmount()).thenReturn(500L);

        List<InvoiceDetail> eligibleInvoiceDetails = List.of(invoiceDetail);
        when(invoiceDetailRepository.findEligibleForTransfer(threeDaysAgo)).thenReturn(eligibleInvoiceDetails);

        // Call the changeBalanceWhenTransfer method directly
        transactionService.changeBalanceWhenTransfer((long) (eventDetail.getCost() * 0.5), eventDetail);

        // Verify interactions
        verify(invoiceDetail).setStatus(PaymentStatus.COMPLETE);
        verify(invoiceDetail).setModifiedDate(any(LocalDateTime.class)); // Verify modified date is set
        verify(invoiceDetailRepository).saveAndFlush(invoiceDetail);
        verify(senderBalance).setAmount(950L); // Verify sender balance deduction
        verify(recipientBalance).setAmount(550L); // Verify recipient balance addition
        verify(balanceRepository, times(2)).saveAndFlush(any(Balance.class)); // Verify balances are saved
    }

    @Test
    public void testProcessBalanceTransfers_Exception() {
        // Set up to throw an exception
        when(invoiceDetailRepository.findEligibleForTransfer(any())).thenThrow(new RuntimeException("Database error"));

        // Assert that the method throws the expected exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            transactionService.changeBalanceWhenTransfer(10L, new EventDetail()); // Call the method that should throw an exception
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Something went wrong", exception.getReason());
    }
}