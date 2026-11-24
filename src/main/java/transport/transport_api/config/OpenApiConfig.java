
package transport.transport_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transportOpenAPI() {

        return new OpenAPI()
            .info(new Info()
                .title("Transport Management API")
                .version("1.0.0")
                .description(
                    "REST API for transport order management, "
                    + "drivers, assignments and attachments."
                )
            )

            .components(new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            );
    }
}
