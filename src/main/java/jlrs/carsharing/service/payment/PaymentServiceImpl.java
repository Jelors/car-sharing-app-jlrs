package jlrs.carsharing.service.payment;

import static jlrs.carsharing.model.UserRole.RoleName;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.mapper.PaymentMapper;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.model.User;
import jlrs.carsharing.repository.PaymentRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.PaymentService;
import jlrs.carsharing.service.RentalService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final RentalService rentalService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(
            PaymentMapper paymentMapper,
            PaymentRepository paymentRepository,
            RentalRepository rentalRepository,
            RentalService rentalService,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.rentalService = rentalService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public List<PaymentResponse> getAllPayments(Long userId) {
        User user = userDetailsService.getCurrentUser();
        if (user.getRoles().stream()
                .anyMatch(r -> r.getRole().equals(RoleName.MANAGER))) {
            return paymentRepository.findAllByRental_User_Id(userId)
                    .stream()
                    .map(paymentMapper::toDto)
                    .toList();
        }

        userId = user.getId();
        return paymentRepository.findAllByRental_User_Id(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponse getPaymentBySessionId(String sessionId) throws IllegalAccessException {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session with ID: {" + sessionId + "} not found!"
                ));
        User currentUser = userDetailsService.getCurrentUser();
        boolean isOwner = payment.getRental().getUser().getId().equals(currentUser.getId());
        boolean isManager = currentUser.getRoles().stream().anyMatch(r -> r.getRole().equals(RoleName.MANAGER));
        if (isOwner || isManager) {
            return paymentMapper.toDto(payment);
        }

        throw new IllegalAccessException("You don't have permissions to check "
                + "other users payments!");
    }

}
