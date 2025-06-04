package dev.biddan.nubblev2.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPiConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Nubble API")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발용 서버")
                ));
    }
}
