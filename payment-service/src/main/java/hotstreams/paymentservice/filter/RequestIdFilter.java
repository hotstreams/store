package hotstreams.paymentservice.filter;

import hotstreams.paymentservice.exception.ExceptionMessage;
import hotstreams.paymentservice.exception.InvalidRequestIdException;
import hotstreams.paymentservice.util.RequestIdValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Objects;

@Component
public class RequestIdFilter extends OncePerRequestFilter {
    public static final String REQUEST_ID_HEADER = "X-REQUEST-ID";

    private final HandlerExceptionResolver exceptionResolver;

    public RequestIdFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(REQUEST_ID_HEADER);
        if (Objects.isNull(header) || !RequestIdValidator.isValid(header)) {
            exceptionResolver.resolveException(request, response, null, new InvalidRequestIdException(ExceptionMessage.INVALID_REQUEST_ID.getMessage()));
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
