package jlrs.carsharing.service.impl.car;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import jlrs.carsharing.dto.car.CarResponse;
import jlrs.carsharing.dto.car.CreateCarRequest;
import jlrs.carsharing.mapper.CarMapper;
import jlrs.carsharing.model.Car;
import jlrs.carsharing.repository.CarRepository;
import jlrs.carsharing.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarResponse insertNewCar(CreateCarRequest createCarRequest) {
        Car car = carMapper.toModel(createCarRequest);
        car.setInventory(1);
        carRepository.save(car);
        return carMapper.toDto(car);
    }

    @Override
    public CarResponse getCarInformation(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Car with id {" + id + "} not found!"
                ));
        return carMapper.toDto(car);
    }

    @Override
    public CarResponse updateCarInformation(Long id, CreateCarRequest updateCarRequest) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Car with id {" + id + "} not found!"
                ));
        car.setModel(updateCarRequest.getModel());
        car.setBrand(updateCarRequest.getBrand());
        car.setDailyFee(updateCarRequest.getDailyFee());
        car.setType(updateCarRequest.getType());

        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarResponse> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public void addInventory(Long carId, int amount) {
        Car car = carRepository.findById(carId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find car with id: {" + carId + "}"
                        ));
        if (amount < 0) {
            throw new IllegalArgumentException("Amount can't be less than 0!");
        }
        car.setInventory(car.getInventory() + amount);
        carRepository.save(car);
    }

    @Override
    public void reduceInventory(Long carId, int amount) {
        Car car = carRepository.findById(carId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Can't find car with id: {" + carId + "}"
                        ));
        if (car.getInventory() < amount) {
            throw new IllegalStateException("Not enough cars in inventory");
        }
        car.setInventory(car.getInventory() - amount);
        carRepository.save(car);
    }
}
