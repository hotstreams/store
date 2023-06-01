package hotstreams.paymentservice.service;

import hotstreams.paymentservice.entity.PaymentEntity;
import hotstreams.paymentservice.entity.PaymentOutboxEntity;
import hotstreams.paymentservice.entity.ProcessedRequestEntity;
import hotstreams.paymentservice.model.PaymentDto;
import hotstreams.paymentservice.repository.PaymentOutboxRepository;
import hotstreams.paymentservice.repository.PaymentRepository;
import hotstreams.paymentservice.repository.ProcessedRequestRepository;
import hotstreams.paymentservice.util.RequestIdValidator;
import hotstreams.paymentservice.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ProcessedRequestRepository processedRequestRepository;
    private final List<PaymentValidator> paymentValidatorList;

    @Override
    @Transactional
    public String createPayment(final PaymentDto paymentDto, final String requestId) {
        Objects.requireNonNull(paymentDto);
        Objects.requireNonNull(requestId);

        validatePayment(paymentDto);

        //TODO: mapper
        final PaymentEntity paymentEntity = PaymentEntity.builder()
                .id(UUID.randomUUID().toString())
                .orderId(paymentDto.getOrderId())
                .cardHolder(paymentDto.getCardHolder())
                .cardData(paymentDto.getCardData())
                .cost(paymentDto.getCost())
                .build();

        final PaymentOutboxEntity paymentOutboxEntity = PaymentOutboxEntity.builder().payment(paymentEntity).build();
        final ProcessedRequestEntity processedRequestEntity = ProcessedRequestEntity.builder().id(requestId).paymentEntity(paymentEntity).build();

        paymentRepository.save(paymentEntity);
        paymentOutboxRepository.save(paymentOutboxEntity);
        processedRequestRepository.save(processedRequestEntity);

        return paymentEntity.getId();
    }

    @Override
    public boolean isProcessed(final String id) {
        Objects.requireNonNull(id);
        return RequestIdValidator.isValid(id) && processedRequestRepository.existsById(id);
    }

    private void validatePayment(final PaymentDto paymentDto) {
        paymentValidatorList.forEach(validator -> validator.validate(paymentDto));
    }
}
