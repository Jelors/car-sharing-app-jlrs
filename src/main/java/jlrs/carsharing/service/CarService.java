package jlrs.carsharing.service;

import java.util.List;
import jlrs.carsharing.dto.car.CarResponse;
import jlrs.carsharing.dto.car.CreateCarRequest;

public interface CarService {
    CarResponse insertNewCar(CreateCarRequest createCarRequest);

    CarResponse getCarInformation(Long id);

    CarResponse updateCarInformation(Long id, CreateCarRequest updateCarRequest);

    List<CarResponse> getAllCars();

    void deleteCar(Long id);

    /*
    inventory management
     */
    void addInventory(Long carId, int amount);

    void reduceInventory(Long carId, int amount);

}
