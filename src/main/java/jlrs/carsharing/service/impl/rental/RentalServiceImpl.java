package jlrs.carsharing.service.impl.rental;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequestDto;
import jlrs.carsharing.dto.rental.RentalDto;
import jlrs.carsharing.mapper.RentalMapper;
import jlrs.carsharing.model.Car;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.repository.CarRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.RentalService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public RentalDto addRental(CreateRentalRequestDto createRentalRequest) {
        Long carId = createRentalRequest.getCarId();

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find car with ID: {" + carId + "}"
                ));

        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(createRentalRequest.getReturnDate());
        rental.setCar(car);
        rental.setUser(userDetailsService.getCurrentUser());

        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public RentalDto addActualReturnDate(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental with ID: {" + rentalId + "} not found!"
                ));
        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);

        Long carId = rental.getCar().getId();

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find car with ID: {" + carId + "}"
                ));
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public RentalDto getRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental with ID: {" + id + "} not found!"
                ));
        return rentalMapper.toDto(rental);
    }

    @Override
    public List<RentalDto> getRentalsByUserIdAndIsActive(Long id, boolean active) {
        return rentalRepository.findAllByUserIdAndActive(id, active)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }
}
