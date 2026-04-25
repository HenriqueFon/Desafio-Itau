package desafio.itau.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Component
@Validated
@ConfigurationProperties(prefix = "app.statistics")
public class StatisticsProperties {

    @Min(value = 1, message = "A janela de estatísticas deve ser de pelo menos 1 segundo.")
    private long windowSeconds = 60;

    public long getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(long windowSeconds) {
        this.windowSeconds = windowSeconds;
    }
}