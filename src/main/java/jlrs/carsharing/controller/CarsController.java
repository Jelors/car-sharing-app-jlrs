package jlrs.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import jlrs.carsharing.dto.car.CarDto;
import jlrs.carsharing.dto.car.CreateCarRequestDto;
import jlrs.carsharing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
@Tag(name = "Cars manager", description = "Endpoints for managing cars")
public class CarsController {
    private final CarService carService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Add new car to DB",
            description = "Insert new car in database"
    )
    public ResponseEntity<CarDto> insertNewCar(
            @RequestBody @Valid CreateCarRequestDto createCarRequest
    ) {
        return new ResponseEntity<>(
                carService.insertNewCar(createCarRequest),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Get information about car",
            description = "Receive detailed information about car"
    )
    public ResponseEntity<CarDto> getCarInformation(@PathVariable Long id) {
        return new ResponseEntity<>(
                carService.getCarInformation(id),
                HttpStatus.OK
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CUSTOMER')")
    @Operation(
            summary = "Get information about all cars",
            description = "Get information about all available cars"
    )
    public ResponseEntity<List<CarDto>> getAllCars() {
        return new ResponseEntity<>(
                carService.getAllCars(),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Update car information",
            description = "Updates car information"
    )
    public ResponseEntity<CarDto> updateCarInformation(
            @PathVariable Long id,
            @RequestBody @Valid CreateCarRequestDto updateCarRequest
    ) {
        return new ResponseEntity<>(
                carService.updateCarInformation(id, updateCarRequest),
                HttpStatus.NO_CONTENT
        );
    }

    @PatchMapping("/{id}/add")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Add amount to car's inventory",
            description = "Adds amount to car's inventory"
    )
    public void addInventory(
            @PathVariable Long id,
            @RequestBody int amount
    ) {
        carService.addInventory(id, amount);
    }

    @PatchMapping("/{id}/reduce")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Minus amount from car's inventory",
            description = "Reduce amount of available car's in inventory"
    )
    public void reduceInventory(
            @PathVariable Long id,
            @RequestBody int amount
    ) {
        carService.reduceInventory(id, amount);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Delete car",
            description = "Deletes car"
    )
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
