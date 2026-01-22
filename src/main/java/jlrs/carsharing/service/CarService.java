package jlrs.carsharing.service;

import java.util.List;
import jlrs.carsharing.dto.car.CarDto;
import jlrs.carsharing.dto.car.CreateCarRequestDto;

public interface CarService {
    CarDto insertNewCar(CreateCarRequestDto createCarRequest);

    CarDto getCarInformation(Long id);

    CarDto updateCarInformation(Long id, CreateCarRequestDto updateCarRequest);

    List<CarDto> getAllCars();

    void deleteCar(Long id);

    /*
    inventory management
     */
    void addInventory(Long carId, int amount);

    void reduceInventory(Long carId, int amount);

}
