package jlrs.carsharing.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import jlrs.carsharing.dto.car.CarResponse;
import jlrs.carsharing.dto.car.CreateCarRequest;
import jlrs.carsharing.model.Car;
import jlrs.carsharing.security.JwtUtil;
import jlrs.carsharing.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CarsController.class)
@AutoConfigureMockMvc(addFilters = false)
class CarsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private CreateCarRequest carRequest;
    private CarResponse carResponse;

    @BeforeEach
    void setUp() {
        carRequest = new CreateCarRequest();
        carRequest.setModel("Tesla Model 3");
        carRequest.setBrand("Tesla");
        carRequest.setType(Car.Type.SEDAN);
        carRequest.setDailyFee(BigDecimal.valueOf(100));

        carResponse = new CarResponse();
        carResponse.setId(1L);
        carResponse.setModel("Tesla Model 3");
        carResponse.setBrand("Tesla");
        carResponse.setType(Car.Type.SEDAN);
        carResponse.setInventory(10);
        carResponse.setDailyFee(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("""
            POST /cars - success
            """)
    void insertNewCar_ValidRequest_ReturnsCreated() throws Exception {
        when(carService.insertNewCar(any(CreateCarRequest.class))).thenReturn(carResponse);

        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.model").value("Tesla Model 3"));
    }

    @Test
    @DisplayName("""
            GET /cars/{id} - success
            """)
    void getCarInformation_ValidId_ReturnsCar() throws Exception {
        when(carService.getCarInformation(1L)).thenReturn(carResponse);

        mockMvc.perform(get("/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Tesla"));
    }

    @Test
    @DisplayName("""
            GET /cars - returns List
            """)
    void getAllCars_ReturnsList() throws Exception {
        when(carService.getAllCars()).thenReturn(List.of(carResponse));

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].model").value("Tesla Model 3"));
    }

    @Test
    @DisplayName("""
            PUT /cars/{id} - success
            """)
    void updateCarInformation_ValidRequest_ReturnsNoContent() throws Exception {
        when(carService.updateCarInformation(anyLong(), any(CreateCarRequest.class)))
                .thenReturn(carResponse);

        mockMvc.perform(put("/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("""
            PATCH /cars/{id}/add - success
            """)
    void addInventory_ValidAmount_ReturnsOk() throws Exception {
        doNothing().when(carService).addInventory(anyLong(), any(Integer.class));

        mockMvc.perform(patch("/cars/1/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("5")) // Відправляємо просто число як тіло
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            DELETE /cars/{id} - success
            """)
    void deleteCar_ValidId_ReturnsOk() throws Exception {
        doNothing().when(carService).deleteCar(1L);

        mockMvc.perform(delete("/cars/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("""
            POST /cars - validation fail
            """)
    void insertNewCar_InvalidRequest_ReturnsBadRequest() throws Exception {
        CreateCarRequest invalidRequest = new CreateCarRequest();

        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}