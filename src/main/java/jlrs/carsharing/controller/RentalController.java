package jlrs.carsharing.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jlrs.carsharing.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
@Tag(name = "Rentals management", description = "Endpoints for managing rents")
public class RentalController {
    private final RentalService rentalService;


}
