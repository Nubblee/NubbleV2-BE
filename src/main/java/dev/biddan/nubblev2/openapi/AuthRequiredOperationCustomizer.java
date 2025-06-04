package dev.biddan.nubblev2.openapi;

import dev.biddan.nubblev2.interceptor.auth.AuthRequired;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class AuthRequiredOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        AuthRequired methodAuth = handlerMethod.getMethodAnnotation(AuthRequired.class);

        if (methodAuth != null) {
            SecurityRequirement securityRequirement = new SecurityRequirement();
            securityRequirement.addList("authSessionCookie");
            operation.addSecurityItem(securityRequirement);
        }

        return operation;
    }
}
