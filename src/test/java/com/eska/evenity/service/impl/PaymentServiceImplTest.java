package com.eska.evenity.service.impl;

import com.eska.evenity.dto.request.PaymentDetailRequest;
import com.eska.evenity.dto.request.PaymentRequest;
import com.eska.evenity.dto.response.PaymentResponse;
import com.eska.evenity.entity.Event;
import com.eska.evenity.entity.Invoice;
import com.eska.evenity.entity.Payment;
import com.eska.evenity.repository.EventDetailRepository;
import com.eska.evenity.repository.EventRepository;
import com.eska.evenity.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RestClient restClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private static final String SECRET_KEY = "mockSecretKey";
    private static final String BASE_SNAP_URL = "http://mock-snap-url.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


}