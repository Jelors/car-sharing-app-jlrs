package jlrs.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RentalService rentalService;

    @InjectMocks
    private NotificationService notificationService;

    private RentalResponse rentalResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2026, 2, 20, 10, 0);
        rentalResponse = new RentalResponse();
        rentalResponse.setId(1L);
        rentalResponse.setCarId(10L);
        rentalResponse.setUserId(5L);
        rentalResponse.setRentalDate(LocalDate.from(now));
        rentalResponse.setReturnDate(LocalDate.from(now.plusDays(3)));
    }

    @Test
    @DisplayName("""
            formatRentalsList - returns empty message when no rentals
            """)
    void formatRentalsList_EmptyList_ReturnsEmptyMessage() {
        when(rentalService.getRentalsByUserIdAndIsActive(null, true)).thenReturn(Collections.emptyList());

        String result = notificationService.formatRentalsList(true);

        assertThat(result).contains("List of (Active) rentals is empty!");
    }

    @Test
    @DisplayName("""
            formatRentalsList - formats multiple rentals correctly
            """)
    void formatRentalsList_WithRentals_ReturnsFormattedString() {
        when(rentalService.getRentalsByUserIdAndIsActive(null, true)).thenReturn(List.of(rentalResponse));

        String result = notificationService.formatRentalsList(true);

        assertThat(result)
                .contains("📋 *List of rentals (Active)*")
                .contains("🆔 *Rental ID:* 1")
                .contains("🚗 *Car ID:* 10");
    }

    @Test
    @DisplayName("""
            formatSingleRental - includes actual return date if present
            """)
    void formatSingleRental_WithActualReturnDate_IncludesIt() {
        LocalDateTime actualReturn = now.plusDays(2);
        rentalResponse.setActualReturnDate(LocalDate.from(actualReturn));

        String result = notificationService.formatSingleRental(rentalResponse);

        assertThat(result).contains("✅ *Actual return date:*");
        assertThat(result).contains(actualReturn.toLocalDate().toString());
    }

    @Test
    @DisplayName("""
            formatOverdueMessage - returns correct overdue alert
            """)
    void formatOverdueMessage_ReturnsAlertString() {
        String result = notificationService.formatOverdueMessage(rentalResponse);

        assertThat(result)
                .contains("⚠️ *OVERDUE RENTAL ALERT* ⚠️")
                .contains("❗ *Status:* Car not returned yet!");
    }
}