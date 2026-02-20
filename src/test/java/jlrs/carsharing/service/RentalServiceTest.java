package jlrs.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jlrs.carsharing.dto.rental.CreateRentalRequest;
import jlrs.carsharing.dto.rental.RentalCreatedEvent;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.mapper.RentalMapper;
import jlrs.carsharing.model.Car;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.model.User;
import jlrs.carsharing.model.UserRole;
import jlrs.carsharing.model.UserRole.RoleName;
import jlrs.carsharing.repository.CarRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.impl.rental.RentalServiceImpl;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private User user;
    private UserRole userRole;
    private Car car;
    private Rental rental;

    @BeforeEach
    void setUp() {
        userRole = new UserRole();
        userRole.setRole(RoleName.CUSTOMER);
        userRole.setId(1L);
        user = new User();
        user.setId(1L);
        user.setRoles(Set.of(userRole));

        car = new Car();
        car.setId(10L);
        car.setInventory(2);
        car.setDailyFee(new BigDecimal("100.00"));

        rental = new Rental();
        rental.setId(100L);
        rental.setUser(user);
        rental.setCar(car);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(3));
    }

    @Test
    @DisplayName("""
            Create a new rental
            """)
    void addRental_ValidRequest_Success() {
        CreateRentalRequest request = new CreateRentalRequest();
        request.setCarId(10L);
        request.setReturnDate(LocalDate.now().plusDays(3));

        when(carRepository.findById(10L)).thenReturn(Optional.of(car));
        when(userDetailsService.getCurrentUser()).thenReturn(user);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(any())).thenReturn(new RentalResponse());

        rentalService.addRental(request);

        assertThat(car.getInventory()).isEqualTo(1);
        verify(carRepository).save(car);
        verify(rentalRepository).save(any(Rental.class));
        verify(eventPublisher).publishEvent(any(RentalCreatedEvent.class));
    }

    @Test
    @DisplayName("""
            No inventory exception
            """)
    void addRental_NoInventory_ThrowsException() {
        car.setInventory(0);
        Long carId = 10L;
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        CreateRentalRequest request = new CreateRentalRequest();
        request.setCarId(carId);

        assertThatThrownBy(() -> rentalService.addRental(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You can't take car that ain't available in our inventory!");
    }

    @Test
    @DisplayName("""
            Set actual return date
            """)
    void addActualReturnDate_Success() throws IllegalAccessException {
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));
        when(userDetailsService.getCurrentUserId()).thenReturn(1L);
        when(carRepository.findById(10L)).thenReturn(Optional.of(car));
        when(rentalRepository.save(any())).thenReturn(rental);

        rentalService.addActualReturnDate(100L);

        assertThat(rental.getActualReturnDate()).isEqualTo(LocalDate.now());
        assertThat(rental.isActive()).isFalse();
        assertThat(car.getInventory()).isEqualTo(3);
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("""
            Someone else's car exception
            """)
    void addActualReturnDate_WrongUser_ThrowsException() {
        when(rentalRepository.findById(100L)).thenReturn(Optional.of(rental));
        when(userDetailsService.getCurrentUserId()).thenReturn(999L);

        assertThatThrownBy(() -> rentalService.addActualReturnDate(100L))
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    @DisplayName("""
            Calculate total
            """)
    void calculateTotal_WithFine_ReturnsCorrectAmount() {
        rental.setActualReturnDate(rental.getReturnDate().plusDays(2));

        BigDecimal total = rentalService.calculateTotal(rental);

        assertThat(total).isEqualByComparingTo("540.00");
    }

    @Test
    @DisplayName("""
            Get rental provides that manager can see all rentals
            """)
    void getRental_ManagerAccess_Success() {
        UserRole managerRole = new UserRole();
        managerRole.setRole(RoleName.MANAGER);
        User manager = new User();
        manager.setId(2L);
        manager.setRoles(Set.of(managerRole));
        manager.setEmail("admin@example.com");
        manager.setFirstName("Admin");
        manager.setLastName("Adminov");
        manager.setPassword("12312321djsnhdjkas");

        rental.setUser(manager);
        rental.setId(10L);

        when(userDetailsService.getCurrentUser()).thenReturn(manager);
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(new RentalResponse());

        RentalResponse result = rentalService.getRental(10L);

        assertNotNull(result);
        verify(rentalRepository).findById(10L);
    }
}