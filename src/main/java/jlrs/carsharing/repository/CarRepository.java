package jlrs.carsharing.repository;

import jlrs.carsharing.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

}
