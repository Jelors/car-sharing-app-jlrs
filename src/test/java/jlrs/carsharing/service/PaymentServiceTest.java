package jlrs.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jlrs.carsharing.mapper.PaymentMapper;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.model.User;
import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.repository.PaymentRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import jlrs.carsharing.service.payment.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private RentalService rentalService;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentMapper paymentMapper;

    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(
                "http://success.url", "http://cancel.url", "whsec_test",
                paymentMapper, paymentRepository, rentalRepository, rentalService, userDetailsService
        );
    }

    @Test
    @DisplayName("""
            Manager should be able to see payments for any user id
            """)
    void getAllPayments_ManagerAccess_ReturnsPayments() {
        Long targetUserId = 99L;
        User manager = new User();
        UserRole role = new UserRole();
        role.setRole(UserRole.RoleName.MANAGER);
        manager.setRoles(Set.of(role));

        when(userDetailsService.getCurrentUser()).thenReturn(manager);
        when(paymentRepository.findAllByRental_User_Id(targetUserId)).thenReturn(List.of(new Payment()));

        paymentService.getAllPayments(targetUserId);

        verify(paymentRepository).findAllByRental_User_Id(targetUserId);
    }

    @Test
    @DisplayName("""
            Customer should only see their own payments
            """)
    void getAllPayments_CustomerAccess_OverridesUserId() {
        Long customerId = 1L;
        Long forbiddenId = 99L;
        User customer = new User();
        customer.setId(customerId);
        customer.setRoles(Collections.emptySet());

        when(userDetailsService.getCurrentUser()).thenReturn(customer);

        paymentService.getAllPayments(forbiddenId);

        verify(paymentRepository).findAllByRental_User_Id(customerId);
        verify(paymentRepository, never()).findAllByRental_User_Id(forbiddenId);
    }

    @Test
    @DisplayName("""
            Get payment by session ID - access denied for other users
            """)
    void getPaymentBySessionId_AccessDenied_ThrowsException() {
        String sessionId = "test_session";

        User current = new User();
        current.setId(1L);
        current.setRoles(new HashSet<>());

        User owner = new User();
        owner.setId(2L);

        Rental rental = new Rental();
        rental.setUser(owner);

        Payment payment = new Payment();
        payment.setRental(rental);

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

        when(userDetailsService.getCurrentUser()).thenReturn(current);
        lenient().when(userDetailsService.getCurrentUserId()).thenReturn(1L);

        assertThrows(IllegalAccessException.class, () ->
                paymentService.getPaymentBySessionId(sessionId)
        );
    }
}
