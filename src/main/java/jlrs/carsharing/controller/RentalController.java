package jlrs.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequest;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
@Tag(name = "Rentals management", description = "Endpoints for managing rents")
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Endpoint that adds new rental",
            description = "Endpoint that adds new rental"
    )
    public ResponseEntity<RentalResponse> addNewRental(
            @RequestBody @Valid CreateRentalRequest createRentalRequest
    ) {
        return new ResponseEntity<>(
                rentalService.addRental(createRentalRequest),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Endpoint that sets actual return date",
            description = "Endpoint that sets actual return date"
    )
    public ResponseEntity<RentalResponse> insertActualReturnDate(@PathVariable Long id) {
        return new ResponseEntity<>(
                rentalService.addActualReturnDate(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Endpoint that returns specific rental by id",
            description = "Endpoint that returns specific rental by id"
    )
    public ResponseEntity<RentalResponse> getSpecificRental(@PathVariable Long id) {
        return new ResponseEntity<>(
                rentalService.getRental(id),
                HttpStatus.OK
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Endpoint that returns rentals list",
            description = "Endpoint that returns rentals list"
                    + " and can be specified by {USER_ID} and {IS_ACTIVE}"
    )
    public ResponseEntity<List<RentalResponse>> getRentals(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "is_active", required = false) Boolean isActive
    ) {
        return new ResponseEntity<>(
                rentalService.getRentalsByUserIdAndIsActive(userId, isActive),
                HttpStatus.OK
        );
    }
}
