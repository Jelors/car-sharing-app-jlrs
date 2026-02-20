package jlrs.carsharing.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequest;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.security.JwtUtil;
import jlrs.carsharing.service.RentalService;
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

@WebMvcTest(RentalController.class)
@AutoConfigureMockMvc(addFilters = false)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RentalService rentalService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private RentalResponse rentalResponse;
    private CreateRentalRequest rentalRequest;

    @BeforeEach
    void setUp() {
        rentalResponse = new RentalResponse();
        rentalResponse.setId(1L);
        rentalResponse.setCarId(10L);
        rentalResponse.setUserId(5L);
        rentalResponse.setRentalDate(LocalDate.from(LocalDateTime.now()));
        rentalResponse.setReturnDate(LocalDate.from(LocalDateTime.now().plusDays(3)));

        rentalRequest = new CreateRentalRequest();
        rentalRequest.setCarId(10L);
        rentalRequest.setReturnDate(LocalDate.from(LocalDateTime.now().plusDays(3)));
    }

    @Test
    @DisplayName("""
            POST /rentals - success
            """)
    void addNewRental_ValidRequest_ReturnsCreated() throws Exception {
        when(rentalService.addRental(any(CreateRentalRequest.class))).thenReturn(rentalResponse);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.carId").value(10L));
    }

    @Test
    @DisplayName("""
            POST /rentals/{id}/return - success
            """)
    void setActualReturnDate_ValidId_ReturnsOk() throws Exception {
        rentalResponse.setActualReturnDate(LocalDate.from(LocalDateTime.now()));
        when(rentalService.addActualReturnDate(1L)).thenReturn(rentalResponse);

        mockMvc.perform(post("/rentals/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualReturnDate").exists());
    }

    @Test
    @DisplayName("""
            GET /rentals/{id} - success
            """)
    void getSpecificRental_ValidId_ReturnsRental() throws Exception {
        when(rentalService.getRental(1L)).thenReturn(rentalResponse);

        mockMvc.perform(get("/rentals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("""
            GET /rentals - should return list with parameters
            """)
    void getRentalsByUserId_WithParams_ReturnsList() throws Exception {
        when(rentalService.getRentalsByUserIdAndIsActive(anyLong(), anyBoolean()))
                .thenReturn(List.of(rentalResponse));

        // Тестуємо з параметрами ?user_id=5&is_active=true
        mockMvc.perform(get("/rentals")
                        .param("user_id", "5")
                        .param("is_active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(5L));
    }

    @Test
    @DisplayName("""
            GET /rentals - should return list without parameters
            """)
    void getRentalsByUserId_NoParams_ReturnsList() throws Exception {
        when(rentalService.getRentalsByUserIdAndIsActive(null, null))
                .thenReturn(List.of(rentalResponse));

        mockMvc.perform(get("/rentals"))
                .andExpect(status().isOk())
                .andExpect(status().isOk());
    }
}