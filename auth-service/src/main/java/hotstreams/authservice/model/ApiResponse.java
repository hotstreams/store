package hotstreams.authservice.model;

import lombok.Data;

@Data
public class ApiResponse {
    private final String status;
    private final String message;
}
