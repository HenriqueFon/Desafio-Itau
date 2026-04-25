package desafio.itau.springboot.dto;

import java.util.DoubleSummaryStatistics;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estatísticas das transações realizadas nos últimos 60 segundos")
public class StatisticsResponse {

    @Schema(
            description = "Quantidade de transações realizadas nos últimos 60 segundos.",
            example = "10"
    )
    private long count;

    @Schema(
            description = "Soma total dos valores das transações realizadas nos últimos 60 segundos.",
            example = "1234.56"
    )
    private double sum;

    @Schema(
            description = "Média dos valores das transações realizadas nos últimos 60 segundos.",
            example = "123.456"
    )
    private double avg;

    @Schema(
            description = "Menor valor transacionado nos últimos 60 segundos.",
            example = "12.34"
    )
    private double min;

    @Schema(
            description = "Maior valor transacionado nos últimos 60 segundos.",
            example = "123.56"
    )
    private double max;

    public StatisticsResponse(DoubleSummaryStatistics stats) {
        if (stats == null || stats.getCount() == 0) {
            this.count = 0;
            this.sum = 0.0;
            this.avg = 0.0;
            this.min = 0.0;
            this.max = 0.0;
            return;
        }

        this.count = stats.getCount();
        this.sum = stats.getSum();
        this.avg = stats.getAverage();
        this.min = stats.getMin();
        this.max = stats.getMax();
    }

    public double getAvg() {
        return avg;
    }

    public long getCount() {
        return count;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getSum() {
        return sum;
    }
}