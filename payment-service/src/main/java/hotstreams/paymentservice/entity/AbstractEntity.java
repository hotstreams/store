package hotstreams.paymentservice.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity {
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant modifiedAt;
    @Version
    private int version;
}
