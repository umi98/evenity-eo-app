package com.eska.evenity.service.impl;

import com.eska.evenity.constant.ApprovalStatus;
import com.eska.evenity.constant.EventProgress;
import com.eska.evenity.constant.ProductUnit;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.EventDetail;
import com.eska.evenity.entity.Product;
import com.eska.evenity.repository.EventDetailRepository;
import com.eska.evenity.repository.EventRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunAsyncTask_Success() {
        // Arrange
        doNothing().when(eventSchedulerService).autoRejectPendingEventDetails();
        doNothing().when(eventSchedulerService).changeProgressionStatus();

        // Act
        CompletableFuture<Void> future = eventSchedulerService.runAsyncTask();

        // Assert
        assertNotNull(future);
        verify(eventSchedulerService, times(1)).autoRejectPendingEventDetails();
        verify(eventSchedulerService, times(1)).changeProgressionStatus();
    }

    @Test
    void testRunAsyncTask_ExceptionHandling() {
        // Arrange
        doThrow(new RuntimeException("Error in autoRejectPendingEventDetails"))
                .when(eventSchedulerService).autoRejectPendingEventDetails();
        doNothing().when(eventSchedulerService).changeProgressionStatus();

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
}