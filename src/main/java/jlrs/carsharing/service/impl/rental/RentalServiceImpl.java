package jlrs.carsharing.service.impl.rental;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequest;
import jlrs.carsharing.dto.rental.RentalCreatedEvent;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.mapper.RentalMapper;
import jlrs.carsharing.model.Car;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.model.User;
import jlrs.carsharing.model.UserRole.RoleName;
import jlrs.carsharing.repository.CarRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.RentalService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public RentalResponse addRental(CreateRentalRequest createRentalRequest) {
        Long carId = createRentalRequest.getCarId();

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find car with ID: {" + carId + "}"
                ));

        if (car.getInventory() <= 0) {
            throw new IllegalArgumentException(
                    "You can't take car that ain't available in our inventory!"
            );
        }

        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(createRentalRequest.getReturnDate());
        rental.setCar(car);
        rental.setUser(userDetailsService.getCurrentUser());

        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);

        RentalResponse rentalResponse = rentalMapper.toDto(rentalRepository.save(rental));
        eventPublisher.publishEvent(new RentalCreatedEvent(rentalResponse)); // sends notification about new rental to TG bot
        return rentalResponse;
    }

    @Override
    @Transactional
    public RentalResponse addActualReturnDate(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental with ID: {" + rentalId + "} not found!"
                ));
        if (rental.getActualReturnDate() != null) {
            throw new IllegalCallerException(
                    "Rental with ID: {" + rentalId + "} already returned!"
            );
        }
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
    public RentalResponse getRental(Long id) {
        User user = userDetailsService.getCurrentUser();

        if (user.getRoles().contains(RoleName.MANAGER)) {
            return rentalMapper.toDto(rentalRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Rental with ID: {" + id + "} not found!"
                    )));
        }

        Rental rental = rentalRepository.findByUserIdAndId(user.getId(), id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental with ID: {" + id + "} not found or you don't have required permission!"
                ));
        return rentalMapper.toDto(rental);
    }

    @Override
    public List<RentalResponse> getRentalsByUserIdAndIsActive(Long id, Boolean active) {
        List<Rental> rentals;
        id = userDetailsService.getCurrentUserId();

        boolean isActive = (active == null) || active;

        if (id != null) {
            rentals = rentalRepository.findAllByUserIdAndActive(id, isActive);
        } else {
            rentals = rentalRepository.findAllByActive(isActive);
        }

        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public BigDecimal calculateTotal(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        if (days <= 1) {
            days = 1;
        }

        BigDecimal baseAmount = rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(days));

        BigDecimal fines = BigDecimal.ZERO;
        if (rental.getActualReturnDate() != null &&
                rental.getActualReturnDate().isAfter(rental.getRentalDate())) {
            long lateDays = ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualReturnDate());
            fines = rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(lateDays)).multiply(BigDecimal.valueOf(1.2)); // 20% fine
        }

        return baseAmount.add(fines).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<RentalResponse> getOverdueRentalsByDate(LocalDate date) {
        List<Rental> rentals = rentalRepository
                .findAllByReturnDateLessThanEqualAndActualReturnDateIsNull(date);

        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }
}
