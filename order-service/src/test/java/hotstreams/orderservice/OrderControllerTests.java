package hotstreams.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.orderservice.configuration.WebSecurityConfig;
import hotstreams.orderservice.entity.OrderEntity;
import hotstreams.orderservice.entity.OrderOutboxEntity;
import hotstreams.orderservice.model.OrderDto;
import hotstreams.orderservice.repository.OrderOutboxRepository;
import hotstreams.orderservice.repository.OrderRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(WebSecurityConfig.class)
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {
    "hotstreams.orderservice.service",
    "hotstreams.orderservice.security"
})
public class OrderControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private OrderOutboxRepository orderOutboxRepository;

    @BeforeEach
    public void setUp() {
        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, OrderEntity.class));
        when(orderOutboxRepository.save(any(OrderOutboxEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, OrderEntity.class));
    }

    @Test
    public void shouldCreateValidOrder() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + buildJWT())
                        .content(convertToBody(createValidOrder()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + buildJWT())
                .content(convertToBody(createInvalidOrder()))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.message").value("Bad request parameters"));
    }

    private OrderDto createValidOrder() {
        return OrderDto.builder()
                .itemIds(List.of(1L, 2L, 3L))
                .totalCost(300)
                .build();
    }

    private OrderDto createInvalidOrder() {
        return OrderDto.builder()
                .itemIds(List.of())
                .build();
    }

    @SneakyThrows
    private String convertToBody(final OrderDto orderDto) {
        return objectMapper.writeValueAsString(orderDto);
    }

    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Value("${auth.jwt.expiration}")
    private long expiration;

    private String buildJWT() {
        return Jwts.builder()
                .setIssuedAt(java.sql.Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(expiration)))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .claim("userId", UUID.randomUUID().toString())
                .claim("authorities", "USER")
                .compact();
    }
}
