package jlrs.carsharing;

import jlrs.carsharing.notification.CarsharingTelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CarsharingApplicationTests {

    @MockitoBean
    private AuthenticationConfiguration authenticationConfiguration;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private CarsharingTelegramBot telegramBot;

    @Test
    void contextLoads() {
    }

}
