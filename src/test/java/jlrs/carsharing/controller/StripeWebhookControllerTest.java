package jlrs.carsharing.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jlrs.carsharing.security.JwtAuthenticationFilter;
import jlrs.carsharing.security.JwtUtil;
import jlrs.carsharing.service.PaymentService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StripeWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
class StripeWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter authenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("""
            POST /webhooks/stripe - success
            """)
    void handleWebhook_ValidSignature_ReturnsOk() throws Exception {
        String payload = "{\"id\": \"evt_123\"}";
        String sigHeader = "t=123,v1=abc";

        doNothing().when(paymentService).handleWebhook(anyString(), anyString());

        mockMvc.perform(post("/webhooks/stripe")
                        .header("Stripe-Signature", sigHeader)
                        .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            POST /webhooks/stripe - missing Header Returns 400
            """)
    void handleWebhook_MissingHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/webhooks/stripe")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}