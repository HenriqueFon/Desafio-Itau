package desafio.itau.springboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI desafioItauOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio Itaú Backend")
                        .description("API REST para recebimento de transações e cálculo de estatísticas dos últimos 60 segundos.")
                        .version("1.0.0"));
    }
}
