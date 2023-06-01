package hotstreams.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {
    @Version
    private Integer version;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant modifiedAt;
}
