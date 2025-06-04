package dev.biddan.nubblev2.openapi;

import dev.biddan.nubblev2.argument.userid.CurrentUserId;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserIdParameterCustomizer implements ParameterCustomizer {
    @Override
    public Parameter customize(Parameter parameter, MethodParameter methodParameter) {

        if (methodParameter.hasParameterAnnotation(CurrentUserId.class)) {
            return null;
        }
        return parameter;
    }
}
