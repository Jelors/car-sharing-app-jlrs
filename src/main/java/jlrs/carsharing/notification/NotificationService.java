package jlrs.carsharing.notification;

import static java.lang.String.format;

import java.util.List;
import java.util.stream.Collectors;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.service.RentalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationService {
    private final RentalService rentalService;

    /*
   method that returns all rentals that was created.
   have two statuses: All Active rentals and Rentals that are not active.
    */
    public String formatRentalsList(Boolean isActive) {
        List<RentalResponse> rentals = rentalService.getRentalsByUserIdAndIsActive(null, isActive);
        String type = (isActive != null && isActive) ? "Active" : "All/Not Active";

        if (rentals.isEmpty()) {
            return "📋 *List of (" + type + ") rentals is empty!*";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("📋 *List of rentals (").append(type).append(")*\n\n");

        String items = rentals.stream()
                .map(this::formatSingleRental)
                .collect(Collectors.joining("\n---\n"));

        stringBuilder.append(items);
        return stringBuilder.toString();
    }

    /*
    method returns formatted RentalResponse dto for correct display in telegram
    if actual return date ain't set yet, returns message without this value
     */
    public String formatSingleRental(RentalResponse rentalResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append(format(
                "🆔 *Rental ID:* %d\n"
                        + "🚗 *Car ID:* %d\n"
                        + "👤 *User ID:* %d\n"
                        + "📅 *Rental date:* %s\n"
                        + "📅 *Return date:* %s\n",
                rentalResponse.getId(),
                rentalResponse.getCarId(),
                rentalResponse.getUserId(),
                rentalResponse.getRentalDate(),
                rentalResponse.getReturnDate()
        ));

        if (rentalResponse.getActualReturnDate() != null) {
            sb.append(format("✅ *Actual return date:* %s\n",
                    rentalResponse.getActualReturnDate()));
        }

        return sb.toString();
    }

    public String formatOverdueMessage(RentalResponse rental) {
        return format(
                "⚠️ *OVERDUE RENTAL ALERT* ⚠️\n"
                        + "🆔 *Rental ID:* %d\n"
                        + "🚗 *Car ID:* %d\n"
                        + "👤 *User ID:* %d\n"
                        + "📅 *Expected Return:* %s\n"
                        + "❗ *Status:* Car not returned yet!",
                rental.getId(),
                rental.getCarId(),
                rental.getUserId(),
                rental.getReturnDate()
        );
    }
}
