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
import jlrs.carsharing.model.UserRole;
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
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.2); // 20% fine

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public RentalResponse addRental(CreateRentalRequest createRentalRequest) {
        Car car = findCarById(createRentalRequest.getCarId());

        if (car.getInventory() <= 0) {
            throw new IllegalArgumentException("Car is not available in inventory!");
        }

        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(createRentalRequest.getReturnDate());
        rental.setCar(car);
        rental.setUser(userDetailsService.getCurrentUser());

        updateCarInventory(car, -1);

        RentalResponse rentalResponse = rentalMapper.toDto(rentalRepository.save(rental));
        eventPublisher.publishEvent(new RentalCreatedEvent(rentalResponse));
        return rentalResponse;
    }

    @Override
    @Transactional
    public RentalResponse addActualReturnDate(Long rentalId) throws IllegalAccessException {
        Rental rental = findRentalById(rentalId);

        if (!rental.getUser().getId().equals(userDetailsService.getCurrentUserId())) {
            throw new IllegalAccessException("You don't have permission to return this rental!");
        }

        if (rental.getActualReturnDate() != null) {
            throw new IllegalCallerException("This car has already been returned!");
        }

        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);

        updateCarInventory(rental.getCar(), 1);

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public RentalResponse getRental(Long id) {
        User currentUser = userDetailsService.getCurrentUser();
        Rental rental = findRentalById(id);

        if (isManager(currentUser) || rental.getUser().getId().equals(currentUser.getId())) {
            return rentalMapper.toDto(rental);
        }

        throw new EntityNotFoundException("Rental with ID: {" + id + "} not found for current user.");
    }

    @Override
    public List<RentalResponse> getRentalsByUserIdAndIsActive(Long userId, Boolean active) {
        User currentUser = userDetailsService.getCurrentUser();
        boolean isActive = (active == null) || active;

        Long targetUserId = isManager(currentUser) ? userId : currentUser.getId();

        List<Rental> rentals = (targetUserId != null)
                ? rentalRepository.findAllByUserIdAndActive(targetUserId, isActive)
                : rentalRepository.findAllByActive(isActive);

        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public BigDecimal calculateTotal(Rental rental) {
        long rentalDays = calculateDaysBetween(rental.getRentalDate(), rental.getReturnDate());
        BigDecimal dailyFee = rental.getCar().getDailyFee();

        BigDecimal baseAmount = dailyFee.multiply(BigDecimal.valueOf(rentalDays));
        BigDecimal fines = calculateFines(rental, dailyFee);

        return baseAmount.add(fines).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<RentalResponse> getOverdueRentalsByDate(LocalDate date) {
        return rentalRepository.findAllByReturnDateLessThanEqualAndActualReturnDateIsNull(date)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    private Rental findRentalById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with ID: " + id));
    }

    private Car findCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with ID: " + id));
    }

    private void updateCarInventory(Car car, int delta) {
        car.setInventory(car.getInventory() + delta);
        carRepository.save(car);
    }

    private boolean isManager(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getRole().equals(UserRole.RoleName.MANAGER));
    }

    private long calculateDaysBetween(LocalDate start, LocalDate end) {
        return Math.max(ChronoUnit.DAYS.between(start, end), 1);
    }

    private BigDecimal calculateFines(Rental rental, BigDecimal dailyFee) {
        if (rental.getActualReturnDate() == null || !rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            return BigDecimal.ZERO;
        }
        long lateDays = ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualReturnDate());
        return dailyFee.multiply(BigDecimal.valueOf(lateDays)).multiply(FINE_MULTIPLIER);
    }
}