package hotstreams.itemservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class ItemEntity extends AbstractEntity {
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "cost", nullable = false)
    @Min(0)
    private Integer cost;
    @Column(name = "count", nullable = false)
    @Min(0)
    private Integer count;
}
