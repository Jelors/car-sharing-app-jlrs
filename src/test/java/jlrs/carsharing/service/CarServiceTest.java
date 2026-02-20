package jlrs.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import jlrs.carsharing.dto.car.CarResponse;
import jlrs.carsharing.dto.car.CreateCarRequest;
import jlrs.carsharing.mapper.CarMapper;
import jlrs.carsharing.model.Car;
import jlrs.carsharing.repository.CarRepository;
import jlrs.carsharing.service.impl.car.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;
    private CarResponse carResponse;
    private CreateCarRequest carRequest;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setBrand("Tesla");
        car.setModel("Model S");
        car.setInventory(5);
        car.setDailyFee(BigDecimal.valueOf(100));
        car.setType(Car.Type.SEDAN);

        carResponse = new CarResponse();
        carResponse.setId(1L);
        carResponse.setBrand("Tesla");
        carResponse.setModel("Model S");

        carRequest = new CreateCarRequest();
        carRequest.setBrand("Tesla");
        carRequest.setModel("Model S");
        carRequest.setDailyFee(BigDecimal.valueOf(100));
        carRequest.setType(Car.Type.SEDAN);
    }

    @Test
    @DisplayName("""
            Insert new car
            """)
    void insertNewCar_ValidRequest_ReturnsResponse() {
        when(carMapper.toModel(carRequest)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carResponse);

        CarResponse result = carService.insertNewCar(carRequest);

        assertThat(result).isNotNull();
        assertThat(car.getInventory()).isEqualTo(1);
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("""
            Return car with existing ID
            """)
    void getCarInformation_ExistingId_ReturnsResponse() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carResponse);

        CarResponse result = carService.getCarInformation(1L);

        assertThat(result).isEqualTo(carResponse);
    }

    @Test
    @DisplayName("""
            If car don't found will be thrown exception
            """)
    void getCarInformation_NonExistingId_ThrowsException() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getCarInformation(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("""
            Increase car's inventory
            """)
    void addInventory_ValidAmount_UpdatesInventory() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        carService.addInventory(1L, 3);

        assertThat(car.getInventory()).isEqualTo(8); // 5 + 3
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("""
            If increasing amount method receives negative value will be thrown exception
            """)
    void addInventory_NegativeAmount_ThrowsException() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThatThrownBy(() -> carService.addInventory(1L, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("""
            Decrease available cars in inventory
            """)
    void reduceInventory_ValidAmount_DecreasesInventory() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        carService.reduceInventory(1L, 2);

        assertThat(car.getInventory()).isEqualTo(3); // 5 - 2
        verify(carRepository).save(car);
    }

    @Test
    @DisplayName("""
            If value <= 0 will be thrown exception
            """)
    void reduceInventory_InsufficientInventory_ThrowsException() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThatThrownBy(() -> carService.reduceInventory(1L, 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Not enough cars in inventory");
    }

    @Test
    @DisplayName("""
            Return list of all available cars
            """)
    void getAllCars_ReturnsList() {
        when(carRepository.findAll()).thenReturn(List.of(car));
        when(carMapper.toDto(any())).thenReturn(carResponse);

        List<CarResponse> result = carService.getAllCars();

        assertThat(result).hasSize(1);
        verify(carRepository).findAll();
    }

    @Test
    @DisplayName("""
            Delete car by ID
            """)
    void deleteCar_CallsRepository() {
        carService.deleteCar(1L);
        verify(carRepository).deleteById(1L);
    }
}