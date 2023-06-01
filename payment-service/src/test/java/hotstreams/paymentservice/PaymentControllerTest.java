package hotstreams.paymentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.paymentservice.controller.PaymentController;
import hotstreams.paymentservice.entity.PaymentEntity;
import hotstreams.paymentservice.entity.PaymentOutboxEntity;
import hotstreams.paymentservice.entity.ProcessedRequestEntity;
import hotstreams.paymentservice.exception.ExceptionMessage;
import hotstreams.paymentservice.filter.RequestIdFilter;
import hotstreams.paymentservice.model.CardData;
import hotstreams.paymentservice.model.CardHolder;
import hotstreams.paymentservice.model.PaymentDto;
import hotstreams.paymentservice.model.ResponseMessage;
import hotstreams.paymentservice.repository.PaymentOutboxRepository;
import hotstreams.paymentservice.repository.PaymentRepository;
import hotstreams.paymentservice.repository.ProcessedRequestRepository;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PaymentController.class)
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = {
        "hotstreams.paymentservice.filter",
        "hotstreams.paymentservice.service",
        "hotstreams.paymentservice.validator",
})
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentRepository paymentRepository;
    @MockBean
    private PaymentOutboxRepository paymentOutboxRepository;
    @MockBean
    private ProcessedRequestRepository processedRequestRepository;

    @BeforeEach
    public void setUp() {
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(invocation -> {
            PaymentEntity entity = invocation.getArgument(0, PaymentEntity.class);
            entity.setId(UUID.randomUUID().toString());
            return entity;
        });
        when(paymentOutboxRepository.save(any(PaymentOutboxEntity.class))).thenAnswer(invocation -> {
            PaymentOutboxEntity entity = invocation.getArgument(0, PaymentOutboxEntity.class);
            entity.setId(1L);
            return entity;
        });
        when(processedRequestRepository.save(any(ProcessedRequestEntity.class))).thenAnswer(invocation -> {
            ProcessedRequestEntity entity = invocation.getArgument(0, ProcessedRequestEntity.class);
            return entity;
        });
    }

    @Test
    public void shouldCreatePayment() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapPaymentToString(createPaymentDto()))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(RequestIdFilter.REQUEST_ID_HEADER, UUID.randomUUID().toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResponseMessage.CREATED.getMessage()));
    }

    @Test
    public void shouldFailWithInvalidRequestIdWhenRequestIdHeaderIsAbsent() throws Exception {
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapPaymentToString(createPaymentDto()))
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.INVALID_REQUEST_ID.getMessage()));
    }

    @SneakyThrows
    private String mapPaymentToString(final PaymentDto paymentDto) {
        return objectMapper.writeValueAsString(paymentDto);
    }

    private PaymentDto createPaymentDto() {
        return PaymentDto.builder()
                .orderId(UUID.randomUUID().toString())
                .cardHolder(createCardHolder())
                .cardData(createCardData())
                .cost(BigDecimal.valueOf(300))
                .build();
    }

    private CardHolder createCardHolder() {
        return CardHolder.builder()
                .name("cardholderName")
                .email("cardholderemail@gmail.com")
                .build();
    }

    private CardData createCardData() {
        return CardData.builder()
                .number("5283678348401696")
                .cvc("007")
                .expiration("01/24")
                .build();
    }
}
