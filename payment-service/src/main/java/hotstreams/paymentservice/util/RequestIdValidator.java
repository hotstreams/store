package hotstreams.paymentservice.util;

import java.util.UUID;

public class RequestIdValidator {
    public static boolean isValid(final String requestId) {
        try {
            UUID id = UUID.fromString(requestId);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
