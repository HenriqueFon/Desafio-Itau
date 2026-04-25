package desafio.itau.springboot.dto;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class TransactionRequest {

    @Schema(
            description = "Valor da transação. Deve ser maior ou igual a zero.",
            example = "123.45",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "O valor da transação é obrigatório.")
    @PositiveOrZero(message = "O valor da transação deve ser maior ou igual a zero.")
    private Double valor;

    @Schema(
            description = "Data e hora da transação no padrão ISO 8601.",
            example = "2026-04-25T10:30:00.000-03:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "A data e hora da transação é obrigatória.")
    private OffsetDateTime dataHora;

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(OffsetDateTime dataHora) {
        this.dataHora = dataHora;
    }
}