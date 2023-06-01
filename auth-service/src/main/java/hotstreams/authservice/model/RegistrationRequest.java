package hotstreams.authservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class RegistrationRequest {
    @NotBlank
    @Length(min = 8, max = 16)
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    @Email
    private String email;
}
