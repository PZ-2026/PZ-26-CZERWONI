package pl.edu.ur.teachly.common.security;

import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.HandlerExceptionResolver;

@ExtendWith(MockitoExtension.class)
@DisplayName("Security entry points – testy jednostkowe")
class SecurityEntryPointTest {

    @Mock private HandlerExceptionResolver resolver;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @Test
    @DisplayName("CustomAuthenticationEntryPoint – deleguje wyjątek do resolvera")
    void authenticationEntryPoint_delegatesToResolver() {
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint(resolver);
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        entryPoint.commence(request, response, ex);

        verify(resolver).resolveException(request, response, null, ex);
    }

    @Test
    @DisplayName("CustomAccessDeniedHandler – deleguje wyjątek do resolvera")
    void accessDeniedHandler_delegatesToResolver() {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler(resolver);
        AccessDeniedException ex = new AccessDeniedException("Denied");

        handler.handle(request, response, ex);

        verify(resolver).resolveException(request, response, null, ex);
    }
}
