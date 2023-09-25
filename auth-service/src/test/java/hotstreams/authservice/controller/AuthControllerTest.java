package hotstreams.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.authservice.controller.AuthController;
import hotstreams.authservice.entity.User;
import hotstreams.authservice.exception.EmailAlreadyExistsException;
import hotstreams.authservice.model.AuthenticationResponse;
import hotstreams.authservice.model.DeviceInfo;
import hotstreams.authservice.model.LoginRequest;
import hotstreams.authservice.model.RegistrationRequest;
import hotstreams.authservice.service.AuthService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldRegisterNewUser() throws Exception {
        when(authService.registerUser(any())).thenReturn(User.builder().username("username").password("password").build());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buildRegistrationRequestBody())
                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User registered username"))
                .andReturn();
    }

    @Test
    public void shouldThrowOnNewUserRegistrationWhenUserAlreadyExists() throws Exception {
        when(authService.registerUser(any())).thenThrow(new EmailAlreadyExistsException("email@gmail.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRegistrationRequestBody())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("email@gmail.com"))
                .andReturn();
    }

    @Test
    public void shouldAuthenticateUser() throws Exception {
        when(authService.authenticateUser(any())).thenReturn(new AuthenticationResponse("jwt", "refreshToken"));

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLogicRequestBody())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("jwt"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andReturn();
    }

    @SneakyThrows
    private String buildRegistrationRequestBody() {
        final RegistrationRequest request = RegistrationRequest.builder()
            .username("username")
            .password("password")
            .email("email@gmail.com")
            .build();
        return objectMapper.writeValueAsString(request);
    }

    @SneakyThrows
    private String buildLogicRequestBody() {
        final LoginRequest request = LoginRequest.builder()
                .username("username")
                .password("password")
                .deviceInfo(DeviceInfo.builder().deviceId("deviceId").build())
                .build();
        return objectMapper.writeValueAsString(request);
    }
}
