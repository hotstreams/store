package hotstreams.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.authservice.model.DeviceInfo;
import hotstreams.authservice.model.LoginRequest;
import hotstreams.authservice.model.RegistrationRequest;
import hotstreams.authservice.service.AuthService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class AuthTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    @DirtiesContext
    public void shouldAuthenticateUser() throws Exception {
        authService.registerUser(RegistrationRequest.builder().username("username").password("password").email("email@gmail.com").build());

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLoginRequestBody())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.jwt").exists());
    }

    @Test
    @DirtiesContext
    public void shouldNotAuthenticateUserWhenDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildLoginRequestBody())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    @DirtiesContext
    public void shouldRegisterUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRegistrationRequestBody())
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User registered username"));
    }

    @Test
    @DirtiesContext
    public void shouldThrowOnNewUserRegistrationWhenUserAlreadyExists() throws Exception {
        authService.registerUser(RegistrationRequest.builder().username("username").password("password").email("email@gmail.com").build());

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

    @SneakyThrows
    private String buildLoginRequestBody() {
        final LoginRequest request = LoginRequest.builder()
                .username("username")
                .password("password")
                .deviceInfo(DeviceInfo.builder().deviceId("deviceId").build())
                .build();
        return objectMapper.writeValueAsString(request);
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
}
