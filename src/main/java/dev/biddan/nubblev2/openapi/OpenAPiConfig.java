package dev.biddan.nubblev2.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPiConfig {

    @Bean
    public OpenAPI openAPI(
            Info apiInfo,
            List<Server> apiServers,
            Components openApiComponents
    ) {
        return new OpenAPI()
                .info(apiInfo)
                .servers(apiServers)
                .components(openApiComponents);
    }

    @Bean
    public Info apiInfo() {
        return new Info()
                .title("Nubble API")
                .version("v1.0.0");
    }

    @Bean
    public List<Server> apiServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("개발용 서버")
        );
    }

    @Bean
    public Components openApiComponents(SecurityScheme authSessionCookieScheme) {
        return new Components()
                .addSecuritySchemes("authSessionCookie", authSessionCookieScheme);
    }

    @Bean
    public SecurityScheme authSessionCookieScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("auth-session-id");
    }
}
