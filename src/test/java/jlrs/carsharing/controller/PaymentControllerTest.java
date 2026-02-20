package jlrs.carsharing.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stripe.exception.StripeException;
import java.math.BigDecimal;
import java.util.List;
import jlrs.carsharing.dto.payment.CheckoutResponseDto;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.exception.GlobalExceptionHandler;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.security.JwtUtil;
import jlrs.carsharing.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({PaymentController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        paymentResponse = new PaymentResponse();
        paymentResponse.setSessionId("session_123");
        paymentResponse.setTotal(BigDecimal.valueOf(500));
        paymentResponse.setStatus(Payment.Status.PENDING);
    }

    @Test
    @DisplayName("""
            GET /payments - should return list for user
            """)
    void getPayments_ValidUserId_ReturnsList() throws Exception {
        when(paymentService.getAllPayments(1L)).thenReturn(List.of(paymentResponse));

        mockMvc.perform(get("/payments")
                        .param("user_id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sessionId").value("session_123"));
    }

    @Test
    @DisplayName("""
            GET /payments/{sessionId} - should return payment by session_id param
            """)
    void getPaymentBySessionId_ValidParam_ReturnsPayment() throws Exception {
        when(paymentService.getPaymentBySessionId("session_123")).thenReturn(paymentResponse);

        mockMvc.perform(get("/payments/some_id")
                        .param("session_id", "session_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session_123"));
    }

    @Test
    @DisplayName("""
            POST /payments/checkout/{rentalId} - should return CheckoutResponse
            """)
    void createCheckout_ValidRentalId_ReturnsCreated() throws Exception {
        CheckoutResponseDto checkoutResponse = new CheckoutResponseDto("http://stripe.com/pay");
        when(paymentService.createCheckout(10L)).thenReturn(checkoutResponse);

        mockMvc.perform(post("/payments/checkout/10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionUrl").value("http://stripe.com/pay"));
    }

    @Test
    @DisplayName("""
            POST /payments/checkout/{rentalId} - should handle StripeException
            """)
    void createCheckout_StripeError_ReturnsInternalServerError() throws Exception {
        when(paymentService.createCheckout(anyLong())).thenThrow(new StripeException("Error", "id", "code", 500) {
        });

        mockMvc.perform(post("/payments/checkout/10"))
                .andExpect(status().isInternalServerError());
    }
}