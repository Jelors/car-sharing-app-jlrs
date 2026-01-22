package jlrs.carsharing.service.impl.rental;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequestDto;
import jlrs.carsharing.dto.rental.RentalDto;
import jlrs.carsharing.mapper.RentalMapper;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.RentalService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public RentalDto addRental(CreateRentalRequestDto createRentalRequest) {
        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(createRentalRequest.getReturnDate());
        rental.setCarId(createRentalRequest.getCarId());
        rental.setUserId(userDetailsService.getCurrentUser());

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public RentalDto addActualReturnDate(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "\"Rental with ID: {" + id + "} not found!\""
                ));
        rental.setActualReturnDate(LocalDate.now());
        rental.setActive(false);
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
    public List<RentalDto> getRentalsByUserIdAndIsActive(Long id, boolean isActive) {
        return rentalRepository.findAllByUserIdAndIsActive(id, isActive)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }
}
